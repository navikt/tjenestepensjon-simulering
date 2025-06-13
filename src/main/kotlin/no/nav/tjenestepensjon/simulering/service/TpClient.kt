package no.nav.tjenestepensjon.simulering.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.ALLE_TP_FORHOLD_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_TSSID_CACHE
import no.nav.tjenestepensjon.simulering.model.domain.HentAlleTPForholdResponseDto
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningMedDato
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpregisteretException
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono

@Service
class TpClient(
    private val webClient: WebClient,
    private val tokenClient: AADClient,
    @Value("\${tp.url}") private var tpUrl: String,
    @Value("\${tp.scope}") private val tpScope: String,
) : Pingable {
    private val log = KotlinLogging.logger {}

    @Cacheable(TP_ORDNING_TSSID_CACHE)
    fun findTssId(tpId: String): String? =
        webClient.get()
            .uri("$tpUrl/api/tpconfig/tssnr/$tpId")
            .exchangeToMono(::handleStringResponse)
            .block()

    @Cacheable(ALLE_TP_FORHOLD_CACHE)
    fun findAlleTPForhold(fnr: String): List<TpOrdningMedDato> {
        return webClient.get()
            .uri("$tpUrl/api/intern/tjenestepensjon/forhold/")
            .headers { setHeaders(it, fnr) }
            .exchangeToMono(::handleAlleTpForholdResponse)
            .onErrorMap(::handleRemoteError)
            .block().orEmpty()
    }

    override fun ping(): PingResponse {
        return try {
            val response = webClient.get()
                .uri("$tpUrl/actuator/health/liveness")
                .headers(::setAuth)
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: "PING OK, ingen response body"

            pingResponse(response)
        } catch (e: WebClientResponseException) {
            "Failed to ping $PROVIDER ${e.responseBodyAsString}".let {
                log.error(e) { it }
                pingResponse(it)
            }
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to ping $PROVIDER with url ${e.uri}" }
            pingResponse("Failed")
        } catch (e: Exception) {
            log.error(e) { "An unexpected error occurred while pinging $PROVIDER ${e.message}" }
            pingResponse("Unexpected error: ${e.message}")
        }
    }

    private fun setHeaders(headers: HttpHeaders, fnr: String) {
        headers["fnr"] = fnr
        setAuth(headers)
    }

    private fun setAuth(headers: HttpHeaders) {
        headers.setBearerAuth(tokenClient.getToken(tpScope))
    }

    private fun handleAlleTpForholdResponse(response: ClientResponse): Mono<List<TpOrdningMedDato>> =
        when (response.statusCode()) {
            HttpStatus.OK -> response.bodyToMono<HentAlleTPForholdResponseDto>().map {
                it.forhold.map { forhold ->
                    TpOrdningMedDato(
                        tpNr = forhold.tpNr,
                        navn = forhold.tpOrdningNavn ?: forhold.tpNr,
                        datoSistOpptjening = forhold.datoSistOpptjening
                    )
                }
            }

            HttpStatus.NOT_FOUND -> Mono.empty()
            else -> Mono.error(handleRemoteError("Received status code ${response.statusCode()} fra $PROVIDER"))
        }


    private fun handleStringResponse(response: ClientResponse): Mono<String> =
        when (response.statusCode()) {
            HttpStatus.OK -> response.bodyToMono<String>()
            HttpStatus.NOT_FOUND -> Mono.empty()
            else -> Mono.error(handleRemoteError(null))
        }

    private fun handleRemoteError(logMessage: String?): TpregisteretException =
        "Error fetching data from TP".let {
            log.error { "$it: $logMessage" }
            TpregisteretException(it)
        }

    private fun handleRemoteError(throwable: Throwable): TpregisteretException =
        handleRemoteError(throwable.message)

    companion object {
        const val PROVIDER = "Tp-registeret"
        const val TJENESTE = "TpForhold"

        private fun pingResponse(message: String) =
            PingResponse(provider = PROVIDER, tjeneste = TJENESTE, melding = message)
    }
}