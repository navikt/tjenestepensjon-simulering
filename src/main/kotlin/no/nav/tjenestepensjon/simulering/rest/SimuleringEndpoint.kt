package no.nav.tjenestepensjon.simulering.rest

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.service.SimuleringService
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToIdPortenException
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes
import org.springframework.web.reactive.function.client.WebClientResponseException

@RestController
class SimuleringEndpoint(
        private val service: SimuleringService,
        private val service2: no.nav.tjenestepensjon.simulering.v2.service.SimuleringService,
        private val metrics: AppMetrics
) {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @PostMapping("/simulering")
    fun simuler(
            @RequestBody body: String,
            @RequestHeader(value = NAV_CALL_ID, required = false) navCallId: String?
    ): ResponseEntity<Any> {
        addHeaderToRequestContext(NAV_CALL_ID, navCallId)
        LOG.info("Processing nav-call-id: {}", getHeaderFromRequestContext(NAV_CALL_ID))
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_CALLS)

        return try {
            try {
                service.simulerOffentligTjenestepensjon(
                        objectMapper.readValue(body, SimulerPensjonRequest::class.java)
                )
            } catch (e: WebClientResponseException) {
                LOG.error("Caught WebClientResponseException in version 1, returns 500 error code.", e)
                e.message to INTERNAL_SERVER_ERROR
            } catch (e: Throwable) {
                LOG.info("Caught exception in version 1,  trying version 2.")
                service2.simulerOffentligTjenestepensjon(
                        objectMapper.readValue(body, no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest::class.java)
                )
            }.let {
                LOG.info("Processing nav-call-id: {}", getHeaderFromRequestContext(NAV_CALL_ID))
                LOG.debug("--Temp Response2: {}", it)
                ResponseEntity(it, OK)
            }
        } catch (e: Throwable) {
            LOG.error("Unable to handle request", e)
            when (e) {
                is JsonParseException -> "Unable to parse body to request." to BAD_REQUEST
                is JsonMappingException -> "Unable to mapping body to request." to BAD_REQUEST
                is ConnectToIdPortenException -> "Unable to to connect with idPorten." to INTERNAL_SERVER_ERROR
                is ConnectToMaskinPortenException -> "Unable to to get token from maskinporten." to INTERNAL_SERVER_ERROR
                is SimuleringException -> e.message to INTERNAL_SERVER_ERROR
                else -> e.message to INTERNAL_SERVER_ERROR
            }.run {
                LOG.error("httpResponse: {}, cause: {}", first, e.message)
                ResponseEntity(first.toString(), second)
            }
        }
    }

    fun addHeaderToRequestContext(key: String, value: String?) {
        if (value != null)
            currentRequestAttributes().setAttribute(key, value, SCOPE_REQUEST)
    }

    fun getHeaderFromRequestContext(key: String) =
            currentRequestAttributes().getAttribute(key, SCOPE_REQUEST)?.toString()

    companion object {
        const val NAV_CALL_ID = "nav-call-id"
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.declaringClass)
    }
}