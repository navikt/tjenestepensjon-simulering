package no.nav.tjenestepensjon.simulering.rest

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_BRUKER_KVALIFISERER_IKKE
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_TP_ORDNING_STOTTES_IKKE
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_ERROR
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_OK
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.exceptions.BrukerKvalifisererIkkeTilTjenestepensjonException
import no.nav.tjenestepensjon.simulering.exceptions.LeveradoerNotFoundException
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.service.TpClient.Companion.PROVIDER
import no.nav.tjenestepensjon.simulering.service.TpClient.Companion.TJENESTE
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.v1.models.DtoToV1DomainMapper.toSimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.service.SimuleringServiceV1
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToIdPortenException
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.toSimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse.Companion.ikkeMedlem
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse.Companion.tpOrdningStoettesIkke
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import no.nav.tjenestepensjon.simulering.v2.service.SimuleringServiceV2
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.lang.reflect.UndeclaredThrowableException

@RestController
class SimuleringEndpoint(
    private val service: SimuleringServiceV1,
    private val service2: SimuleringServiceV2,
    private val tpClient: TpClient,
    private val restGatewayWebClient: WebClient,
    private val restClient: RestClient, //TODO remove etter test
    @Value("\${oftp.before2025.spk.maskinportenscope}") val spkScope: String,
    private val stillingsprosentService: StillingsprosentService,
    @Qualifier("tpLeverandor") private val tpLeverandorList: List<TpLeverandor>,
    private val asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>,
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

        return try {
            val fnr = FNR(body.fnr)
            val tpOrdningAndLeverandorMap = tpClient.findForhold(fnr)
                .mapNotNull { forhold -> tpClient.findTssId(forhold.ordning)?.let { TPOrdningIdDto(tpId = forhold.ordning, tssId = it) } }//tpClient.findTpLeverandorName(tpOrdning)
                .let(::getTpLeverandorer)
            val stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(fnr, tpOrdningAndLeverandorMap)
            val tpOrdning = stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.tpOrdningStillingsprosentMap)

            metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_OK)
            val tpLeverandor = tpOrdningAndLeverandorMap[tpOrdning]!!

            if (tpLeverandor.impl == REST) {
                log.debug { "Request simulation from ${tpLeverandor.name} using REST" }
                val response = service2.simulerOffentligTjenestepensjon(
                    body.toSimulerPensjonRequestV2(),
                    stillingsprosentResponse,
                    tpOrdning,
                    tpLeverandor
                )
                metrics.incrementRestCounter(tpLeverandor.name, "OK")
                log.debug { "Returning response: ${filterFnr(response.toString())}" }
                ResponseEntity(response, OK)
            } else {
                log.debug { "Request simulation from ${tpLeverandor.name} using SOAP" }
                val response = service.simulerOffentligTjenestepensjon(
                    body.toSimulerPensjonRequestV1(),
                    stillingsprosentResponse,
                    tpOrdning,
                    tpLeverandor
                )
                log.debug { "Returning response: ${filterFnr(response.toString())}" }
                ResponseEntity(response, OK)
            }
        } catch (e: NoTpOrdningerFoundException) {
            log.debug { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No TP-forhold found for person.""" }
            ResponseEntity.ok(SimulerOffentligTjenestepensjonResponse.ikkeMedlem())
        } catch (e: JsonParseException) {
            log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. Unable to parse body to request.""" }
            ResponseEntity.badRequest().build()
        } catch (e: JsonMappingException) {
            log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. Unable to map body to request.""" }
            ResponseEntity.badRequest().build()
        } catch (e: LeveradoerNotFoundException) {
            metrics.incrementCounter(APP_TOTAL_SIMULERING_TP_ORDNING_STOTTES_IKKE)
            log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. No supported TP-Ordning found.""" }
            ResponseEntity.ok(SimulerOffentligTjenestepensjonResponse.tpOrdningStoettesIkke())
        } catch (e: BrukerKvalifisererIkkeTilTjenestepensjonException) {
            metrics.incrementCounter(APP_TOTAL_SIMULERING_BRUKER_KVALIFISERER_IKKE)
            log.warn { """Request with nav-call-id ${getHeaderFromRequestContext(NAV_CALL_ID)}. Bruker kvalifiserer ikke til tjenestepensjon. ${e.message}""" }
            ResponseEntity.status(HttpStatus.CONFLICT).body(e.message ?: "Bruker kvalifiserer ikke til tjenestepensjon")
        } catch (e: Throwable) {
            when (e) {
                is ConnectToIdPortenException -> "Unable to to connect with idPorten." to INTERNAL_SERVER_ERROR
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

                ResponseEntity(first, second)
            }
        }
    }

    fun addHeaderToRequestContext(key: String, value: String?) {
        if (value != null) currentRequestAttributes().setAttribute(key, value, SCOPE_REQUEST)
    }

    fun getHeaderFromRequestContext(key: String) =
        currentRequestAttributes().getAttribute(key, SCOPE_REQUEST)?.toString()

    private fun getTpLeverandorer(tpOrdningIdDtoList: List<TPOrdningIdDto>): MutableMap<TPOrdningIdDto, TpLeverandor> {
        if (tpOrdningIdDtoList.isEmpty()) throw LeveradoerNotFoundException("TSSnr not found for any tpOrdning.")
        return asyncExecutor.executeAsync(tpOrdningIdDtoList.associateWith { tpOrdning ->
            FindTpLeverandorCallable(tpOrdning, tpClient, tpLeverandorList, metrics)
        }).resultMap.apply {
            if (isEmpty()) throw LeveradoerNotFoundException("No Tp-leverandoer found for person.")
        }
    }

    @GetMapping("/simulering/ping")
    fun ping() : List<PingResponse> {
        try{
            val resp = restGatewayWebClient.post()
                .uri("/medlem/pensjon/prognose/v1")
                .header("scope", spkScope)
                .bodyValue(dummyRequest())
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: "Received no body"
            return listOf(PingResponse(PROVIDER, TJENESTE, resp))
        } catch (e: WebClientResponseException) {
            if (e.statusCode.is4xxClientError || e.statusCode.is5xxServerError || e.statusCode.is2xxSuccessful){
                log.error(e) { "Successfully connected to $PROVIDER, received ${e.statusText} (${e.statusCode})" }
                return listOf(PingResponse(PROVIDER, TJENESTE, "Failed"))
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

    @GetMapping("/fss/simulering/ping")
    fun pingFraFss() : List<PingResponse> {
        try{
            val resp = restClient.ping(dummyRequest())
            return listOf(PingResponse(PROVIDER, TJENESTE, resp))
        } catch (e: WebClientResponseException) {
            if (e.statusCode.is4xxClientError || e.statusCode.is5xxServerError || e.statusCode.is2xxSuccessful){
                log.error(e) { "Successfully connected to $PROVIDER, received ${e.statusText} (${e.statusCode})" }
                return listOf(PingResponse(PROVIDER, TJENESTE, "Failed"))
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

    private fun dummyRequest(fnr: String = "01015512345") = SimulerPensjonRequestV2(
        fnr = FNR(fnr),
        fodselsdato = "01-01-1955",
        sisteTpnr = "3010",
        sivilstandkode = SivilstandCodeEnum.UGIF,
        inntektListe = emptyList(),
        simuleringsperiodeListe = emptyList(),
        simuleringsdataListe = emptyList()
    )

    companion object {
        const val NAV_CALL_ID = "nav-call-id"
        const val PROVIDER = "SPK"
        const val TJENESTE = "tjenestepensjon"
        private val fnrFilterRegex = "(?<=\\d{6})\\d{5}".toRegex()

        fun filterFnr(s: String) = fnrFilterRegex.replace(s, "*****")
    }
}
