package no.nav.tjenestepensjon.simulering.rest

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_ERROR
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_OK
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.exceptions.LeveradoerNotFoundException
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.v1.models.DtoToV1DomainMapper.toSimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.service.SimuleringServiceV1
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToIdPortenException
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.toSimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.service.SimuleringServiceV2
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.lang.reflect.UndeclaredThrowableException

@RestController
class SimuleringEndpoint(
    private val service: SimuleringServiceV1,
    private val service2: SimuleringServiceV2,
    private val tpClient: TpClient,
    private val stillingsprosentService: StillingsprosentService,
    @Qualifier("tpLeverandor") private val tpLeverandorList: List<TpLeverandor>,
    private val asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>,
    private val metrics: AppMetrics
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/simulering")
    fun simuler(
        @RequestBody body: SimulerOffentligTjenestepensjonRequest,
        @RequestHeader(value = NAV_CALL_ID, required = false) navCallId: String?
    ): ResponseEntity<Any> {
        addHeaderToRequestContext(NAV_CALL_ID, navCallId)
        log.info("Processing nav-call-id: ${getHeaderFromRequestContext(NAV_CALL_ID)}")
        log.debug("Received request: $body")
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_CALLS)

        return try {
            val fnr = FNR(body.fnr)
            val tpOrdningAndLeverandorMap = tpClient.findForhold(fnr)
                .mapNotNull { forhold ->
                    tpClient.findTssId(forhold.ordning)?.let { TPOrdning(tpId = forhold.ordning, tssId = it) }
                }
                .let(::getTpLeverandorer)
            val stillingsprosentResponse =
                stillingsprosentService.getStillingsprosentListe(fnr, tpOrdningAndLeverandorMap)
            val tpOrdning =
                stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.tpOrdningStillingsprosentMap)

            metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_OK)
            val tpLeverandor = tpOrdningAndLeverandorMap[tpOrdning]!!

            if (tpLeverandor.impl == REST) {
                log.debug("Request simulation from ${tpLeverandor.name} using REST")
                val response = service2.simulerOffentligTjenestepensjon(
                    body.toSimulerPensjonRequestV2(),
                    stillingsprosentResponse,
                    tpOrdning,
                    tpLeverandor
                )
                metrics.incrementRestCounter(tpLeverandor.name, "OK")
                log.debug("Returning response: ${filterFnr(response.toString())}")
                ResponseEntity(response, OK)
            } else {
                log.debug("Request simulation from ${tpLeverandor.name} using SOAP")
                val response = service.simulerOffentligTjenestepensjon(
                    body.toSimulerPensjonRequestV1(),
                    stillingsprosentResponse,
                    tpOrdning,
                    tpLeverandor
                )
                log.debug("Returning response: ${filterFnr(response.toString())}")
                ResponseEntity(response, OK)
            }
        } catch (e: NoTpOrdningerFoundException) {
            log.debug("""Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No TP-forhold found for person.""")
            ResponseEntity.notFound().build()
        } catch (e: JsonParseException) {
            log.warn("""Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. Unable to parse body to request.""")
            ResponseEntity.badRequest().build()
        } catch (e: JsonMappingException) {
            log.warn("""Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. Unable to map body to request.""")
            ResponseEntity.badRequest().build()
        } catch (e: LeveradoerNotFoundException) {
            log.warn("""Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No supported TP-Ordning found.""")
            ResponseEntity.notFound().build()
        } catch (e: Throwable) {
            when (e) {
                is ConnectToIdPortenException -> "Unable to to connect with idPorten." to INTERNAL_SERVER_ERROR
                is ConnectToMaskinPortenException -> "Unable to to get token from maskinporten." to INTERNAL_SERVER_ERROR
                is WebClientResponseException -> "Caught WebClientResponseException in version 1" to INTERNAL_SERVER_ERROR
                is SimuleringException -> e.message to INTERNAL_SERVER_ERROR
                is UndeclaredThrowableException -> e.run {
                    log.error("UndeclaredThrowableException received. $cause")
                    cause?.message to INTERNAL_SERVER_ERROR
                }

                else -> e::class.qualifiedName to INTERNAL_SERVER_ERROR
            }.run {
                log.error(
                    """
                    Unable to handle request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}:
                    httpResponse: ${second.value()} - $first, cause: ${e.message}
                    """.trimIndent(), e
                )

                if (e is SimuleringException) {
                    metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_FEIL)
                } else {
                    metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_ERROR)
                }

                ResponseEntity(first, second)
            }
        }
    }

    fun addHeaderToRequestContext(key: String, value: String?) {
        if (value != null) currentRequestAttributes().setAttribute(key, value, SCOPE_REQUEST)
    }

    fun getHeaderFromRequestContext(key: String) =
        currentRequestAttributes().getAttribute(key, SCOPE_REQUEST)?.toString()

    private fun getTpLeverandorer(tpOrdningList: List<TPOrdning>): MutableMap<TPOrdning, TpLeverandor> {
        if (tpOrdningList.isEmpty()) throw LeveradoerNotFoundException("TSSnr not found for any tpOrdning.")
        return asyncExecutor.executeAsync(tpOrdningList.associateWith { tpOrdning ->
            FindTpLeverandorCallable(tpOrdning, tpClient, tpLeverandorList, metrics)
        }).resultMap.apply {
            if (isEmpty()) throw LeveradoerNotFoundException("No Tp-leverandoer found for person.")
        }
    }

    companion object {
        const val NAV_CALL_ID = "nav-call-id"

        private val fnrFilterRegex = "(?<=\\d{6})\\d{5}".toRegex()

        fun filterFnr(s: String) = fnrFilterRegex.replace(s, "*****")
    }
}
