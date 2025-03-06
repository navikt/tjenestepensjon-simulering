package no.nav.tjenestepensjon.simulering.service

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_LEVERANDOR_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_PERSON_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_TSSID_CACHE
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningDto
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.HateoasWrapper
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpregisteretException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.Exceptions
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TpClient(
    private val webClient: WebClient,
    private val aadClient: AADClient,
    private val jsonMapper: JsonMapper,
    @Value("\${tp.url}") private var tpUrl: String,
    @Value("\${tp.scope}") private val tpScope: String,
) : Pingable {
    private val log = KotlinLogging.logger {}

    @Cacheable(TP_ORDNING_PERSON_CACHE)
    fun findForhold(fnr: String) = try {
        webClient.get()
            .uri("$tpUrl/api/tjenestepensjon/forhold")
            .headers {
                it["fnr"] = fnr
                it.setBearerAuth(aadClient.getToken(tpScope))
            }.exchangeToFlux { clientResponse ->
                when (clientResponse.statusCode().value()) {
                    200 -> clientResponse.bodyToMono<String>().flatMapIterable {
                        try {
                            if (it.isBlank() || it == "{}") emptyList()
                            else jsonMapper.readValue<HateoasWrapper>(it).embedded.forholdModelList
                        } catch (t: Throwable) {
                            log.error(t) { "Failed to parse response from TP, with body: $it" }
                            throw t
                        }
                    }.doOnComplete {
                        log.info { "Successfully fetched data." }
                    }

                    404 -> Flux.empty()
                    else -> clientResponse.bodyToFlux<String>().defaultIfEmpty("<NULL>").flatMap { body ->
                        Flux.error(handleRemoteError("Status code ${clientResponse.statusCode()} with message: $body}"))
                    }
                }
            }.onErrorMap {
                if (it !is ResponseStatusException && it !is NoTpOrdningerFoundException) handleRemoteError(it.message) else it
            }.toIterable().toList().takeUnless { it.isEmpty() }
            ?: emptyList()
    } catch (ex: RuntimeException) {
        throw Exceptions.unwrap(ex)
    }

    @Cacheable(TP_ORDNING_LEVERANDOR_CACHE)
    fun findTpLeverandorName(tpOrdning: TPOrdningIdDto): String? =
        webClient.get().uri("$tpUrl/api/tpconfig/tpleverandoer/${tpOrdning.tpId}").exchangeToMono {
            when (it.statusCode().value()) {
                200 -> it.bodyToMono<String>()
                404 -> Mono.empty()
                else -> Mono.error(handleRemoteError(null))
            }
        }.block()

    @Cacheable(TP_ORDNING_TSSID_CACHE)
    fun findTssId(tpId: String): String? =
        webClient.get().uri("$tpUrl/api/tpconfig/tssnr/$tpId").exchangeToMono {
            when (it.statusCode().value()) {
                200 -> it.bodyToMono<String>()
                404 -> Mono.empty()
                else -> Mono.error(handleRemoteError(null))
            }
        }.block()

    fun findTPForhold(fnr: String): List<TpOrdningDto> {
        return webClient.get()
            .uri("$tpUrl/api/tjenestepensjon/aktiveOrdninger")
            .headers {
                it["fnr"] = fnr
                it.setBearerAuth(aadClient.getToken(tpScope))
            }.exchangeToMono {
                when (it.statusCode().value()) {
                    200 -> it.bodyToMono<List<TpOrdningDto>>()
                    404 -> Mono.empty()
                    else -> Mono.error(handleRemoteError("Received status code ${it.statusCode()} fra tpregisteret")) //TODO bedre feilhåndtering i alle funksjonene
                }
            }
            .onErrorMap { handleRemoteError(it.message) }
            .block() ?: emptyList()
    }

    fun handleRemoteError(logMessage: String?): TpregisteretException {
        log.error { "Error fetching data from TP: $logMessage" }
        return TpregisteretException("Error fetching data from TP")
    }

    override fun ping(): PingResponse {
        try{
            val response = webClient.get()
                .uri("$tpUrl/actuator/health/liveness")
                .headers { it.setBearerAuth(aadClient.getToken(tpScope)) }
                .retrieve().bodyToMono(String::class.java)
                .block() ?: "PING OK, ingen response body"
            return PingResponse(PROVIDER, TJENESTE, response)
        } catch (e: WebClientResponseException) {
            val errorMsg = "Failed to ping $PROVIDER ${e.responseBodyAsString}"
            log.error(e) { errorMsg }
            return PingResponse(PROVIDER, TJENESTE, errorMsg)
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to ping $PROVIDER with url ${e.uri}" }
            return PingResponse(PROVIDER, TJENESTE, "Failed")
        } catch (e: Exception) {
            log.error(e) { "An unexpected error occurred while pinging $PROVIDER ${e.message}" }
            return PingResponse(PROVIDER, TJENESTE, "Unexpected error: ${e.message}")
        }
    }

    companion object {
        const val PROVIDER = "Tp-registeret"
        const val TJENESTE = "TpForhold"
    }
}
