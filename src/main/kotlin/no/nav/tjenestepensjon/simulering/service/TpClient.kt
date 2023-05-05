package no.nav.tjenestepensjon.simulering.service

import com.fasterxml.jackson.databind.json.JsonMapper
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_LEVERANDOR_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_PERSON_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_TSSID_CACHE
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.HateoasWrapper
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.server.ResponseStatusException
import reactor.core.Exceptions
import reactor.core.publisher.Flux

@Service
class TpClient(
    private val webClient: WebClient,
    private val aadClient: AADClient,
    private val jsonMapper: JsonMapper,
    @Value("\${TP_URL}") private var tpUrl: String,
    @Value("\${TP_SCOPE}") private val tpScope: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getTpOrdningerForPerson(fnr: FNR) = findForhold(fnr).map {
        TPOrdning(tpId = it.ordning, tssId = findTssId(it.ordning))
    }

    @Cacheable(TP_ORDNING_PERSON_CACHE)
    fun findForhold(fnr: FNR) = try {
        webClient.get()
            .uri("$tpUrl/api/tjenestepensjon/forhold")
            .headers {
                it["fnr"] = fnr.fnr
                it.setBearerAuth(aadClient.getToken(tpScope))
            }.exchangeToFlux { clientResponse ->
                when (clientResponse.statusCode().value()) {
                    200 -> clientResponse.bodyToMono<HateoasWrapper>().flatMapIterable {
                        it.embedded.forholdDtoList
                    }.doOnComplete {
                        log.info("Successfully fetched data.")
                    }.onErrorContinue { e, v ->
                        log.error("Failed to parse response from TP.", e)
                    }

                    404 -> Flux.empty()
                    else -> clientResponse.bodyToFlux<String>().defaultIfEmpty("<NULL>").flatMap { body ->
                        Flux.error(badGateway("Status code ${clientResponse.statusCode()} with message: $body}"))
                    }
                }
            }.onErrorMap {
                if (it !is ResponseStatusException && it !is NoTpOrdningerFoundException) badGateway(it.message) else it
            }.toIterable().toList().takeUnless { it.isEmpty() }
            ?: throw NoTpOrdningerFoundException("No Tp-ordning found for person.")
    } catch (ex: RuntimeException) {
        throw Exceptions.unwrap(ex)
    }

    @Cacheable(TP_ORDNING_LEVERANDOR_CACHE)
    fun findTpLeverandor(tpOrdning: TPOrdning): String =
        webClient.get().uri("$tpUrl/api/tpconfig/tpleverandoer/${tpOrdning.tpId}").retrieve().bodyToMono<String>()
            .block()!!

    @Cacheable(TP_ORDNING_TSSID_CACHE)
    fun findTssId(tpId: String): String =
        webClient.get().uri("$tpUrl/api/tpconfig/tssnr/$tpId").retrieve().bodyToMono<String>().block()!!

    fun badGateway(logMessage: String?): ResponseStatusException {
        log.error("Error fetching data from TP: $logMessage")
        return ResponseStatusException(HttpStatus.BAD_GATEWAY)
    }
}
