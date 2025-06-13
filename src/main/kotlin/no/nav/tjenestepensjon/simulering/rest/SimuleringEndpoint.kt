package no.nav.tjenestepensjon.simulering.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_BRUKER_KVALIFISERER_IKKE
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_TP_ORDNING_STOTTES_IKKE
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_ERROR
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_OK
import no.nav.tjenestepensjon.simulering.exceptions.BrukerKvalifisererIkkeTilTjenestepensjonException
import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningMapper.mapTilTpOrdningFullDto
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.toSimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse.Companion.ikkeMedlem
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse.Companion.tpOrdningStoettesIkke
import no.nav.tjenestepensjon.simulering.v2.service.SPKTjenestepensjonServicePre2025
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpregisteretException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.lang.reflect.UndeclaredThrowableException

@RestController
class SimuleringEndpoint(
    private val spkTjenestepensjonServicePre2025: SPKTjenestepensjonServicePre2025,
    private val tpClient: TpClient,
    private val spkStillingsprosentService: StillingsprosentService,
    private val metrics: AppMetrics,
) {
    private val log = KotlinLogging.logger {}

    @PostMapping("/simulering")
    fun simuler(
        @RequestBody body: SimulerOffentligTjenestepensjonRequest,
        @RequestHeader(value = NAV_CALL_ID, required = false) navCallId: String?
    ): ResponseEntity<Any> {
        addHeaderToRequestContext(NAV_CALL_ID, navCallId)
        log.info { "Processing nav-call-id: ${getHeaderFromRequestContext(NAV_CALL_ID)}" }
        log.debug { "Received request: $body" }
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_CALLS)

        try {
            val fnr = body.fnr
            val alleForhold: List<TpOrdningFullDto> = tpClient.findAlleTPForhold(fnr)
                .mapNotNull { forhold -> tpClient.findTssId(forhold.tpNr)
                    ?.let { TPOrdningIdDto(tpId = forhold.tpNr, tssId = it) }
                    ?.let { mapTilTpOrdningFullDto(forhold, it) } }

            if (alleForhold.isEmpty()){
                log.debug { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No TP-forhold found for person.""" }
                return ResponseEntity.ok(SimulerOffentligTjenestepensjonResponse.ikkeMedlem())
            }

            val spkMedlemskap = alleForhold.firstOrNull { it.tpNr == "3010" || it.tpNr == "3060" }

            if (spkMedlemskap == null) {
                val firstTPOrdningPaaListen = alleForhold.first()
                val tpNr = firstTPOrdningPaaListen.tpNr
                val name = firstTPOrdningPaaListen.navn
                metrics.incrementCounterWithTag(AppMetrics.Metrics.TP_REQUESTED_LEVERANDOR, "$tpNr $name")
                metrics.incrementCounter(APP_TOTAL_SIMULERING_TP_ORDNING_STOTTES_IKKE)
                log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No supported TP-Ordning found.""" }
                return ResponseEntity.ok(SimulerOffentligTjenestepensjonResponse.tpOrdningStoettesIkke())
            }
            metrics.incrementCounterWithTag(AppMetrics.Metrics.TP_REQUESTED_LEVERANDOR, "${spkMedlemskap.tpNr} $PROVIDER")

            val stillingsprosentListe = spkStillingsprosentService.getStillingsprosentListe(fnr, spkMedlemskap)

            if (stillingsprosentListe.isEmpty()){
                log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No stillingsprosent found.""" }
                return ResponseEntity.internalServerError().build()
            }
            metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_OK)

            log.debug { "Request simulation from SPK using REST" }
            val response = spkTjenestepensjonServicePre2025.simulerOffentligTjenestepensjon(
                body.toSimulerPensjonRequestV2(),
                stillingsprosentListe,
                spkMedlemskap,
            )
            metrics.incrementRestCounter(PROVIDER, "OK")
            log.debug { "Returning response: ${filterFnr(response.toString())}" }
            return ResponseEntity(response, OK)
        } catch (e: TpregisteretException) {
            log.error(e) { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. failed.""" }
            return ResponseEntity.internalServerError().build()
        } catch (e: BrukerKvalifisererIkkeTilTjenestepensjonException) {
            metrics.incrementCounter(APP_TOTAL_SIMULERING_BRUKER_KVALIFISERER_IKKE)
            log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. Bruker kvalifiserer ikke til tjenestepensjon. ${e.message}""" }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message ?: "Bruker kvalifiserer ikke til tjenestepensjon")
        } catch (e: Throwable) {
            when (e) {
                is ConnectToMaskinPortenException -> "Unable to to get token from maskinporten." to INTERNAL_SERVER_ERROR
                is WebClientResponseException -> "Caught WebClientResponseException in version 1" to INTERNAL_SERVER_ERROR
                is SimuleringException -> e.message to INTERNAL_SERVER_ERROR
                is UndeclaredThrowableException -> e.run {
                    log.error { "UndeclaredThrowableException received. $cause" }
                    cause?.message to INTERNAL_SERVER_ERROR
                }

                else -> e::class.qualifiedName to INTERNAL_SERVER_ERROR
            }.run {
                log.error(
                    e
                ) {
                    "${
                        """
                                Unable to handle request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}:
                                httpResponse: ${second.value()} - $first, cause: ${e.message}
                                """.trimIndent()
                    }"
                }

                if (e is SimuleringException) {
                    metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_FEIL)
                } else {
                    metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_ERROR)
                }

                return ResponseEntity(first, second)
            }
        }
    }

    fun addHeaderToRequestContext(key: String, value: String?) {
        if (value != null) currentRequestAttributes().setAttribute(key, value, SCOPE_REQUEST)
    }

    fun getHeaderFromRequestContext(key: String) =
        currentRequestAttributes().getAttribute(key, SCOPE_REQUEST)?.toString()

    @GetMapping("/simulering/ping")
    fun ping(): List<PingResponse> {
        try {
            val resp = spkTjenestepensjonServicePre2025.ping()
            return listOf(PingResponse(PROVIDER, TJENESTE, resp))
        } catch (e: WebClientResponseException) {
            if (e.statusCode.is4xxClientError || e.statusCode.value() == 502 || e.statusCode.is2xxSuccessful) {
                val melding = "Successfully connected to $PROVIDER, received ${e.statusText} (${e.statusCode})"
                log.info(e) { melding }
                return listOf(PingResponse(PROVIDER, TJENESTE, melding))
            }
            val errorMsg = "Failed to ping $PROVIDER ${e.responseBodyAsString}"
            log.error(e) { errorMsg }
            return listOf(PingResponse(PROVIDER, TJENESTE, errorMsg))
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to ping $PROVIDER with url ${e.uri}" }
            return listOf(PingResponse(PROVIDER, TJENESTE, "Failed"))
        } catch (e: Exception) {
            log.error(e) { "An unexpected error occurred while pinging $PROVIDER ${e.message}" }
            return listOf(PingResponse(PROVIDER, TJENESTE, "Unexpected error: ${e.message}"))
        }
    }

    companion object {
        const val NAV_CALL_ID = "nav-call-id"
        const val PROVIDER = "SPK"
        const val TJENESTE = "tjenestepensjon"
        private val fnrFilterRegex = "(?<=\\d{6})\\d{5}".toRegex()

        fun filterFnr(s: String) = fnrFilterRegex.replace(s, "*****")
    }
}
