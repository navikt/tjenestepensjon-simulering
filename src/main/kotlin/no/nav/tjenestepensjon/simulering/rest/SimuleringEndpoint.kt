package no.nav.tjenestepensjon.simulering.rest

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.util.getHeaderFromRequestContext
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

@RestController
class SimuleringEndpoint(private val service: SimuleringService, private val metrics: AppMetrics) {
    @RequestMapping(value = ["/simulering"], method = [RequestMethod.POST])
    fun simuler(@RequestBody request: SimulerPensjonRequest, @RequestHeader(value = "nav-call-id", required = false) navCallId: String?): ResponseEntity<SimulerOffentligTjenestepensjonResponse> {

        addHeaderToRequestContext("nav-call-id", navCallId)
        LOG.info("Processing nav-call-id: {}, request: {}", getHeaderFromRequestContext("nav-call-id"), request.toString())
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS)
        val response = service.simulerOffentligTjenestepensjon(request)
        LOG.info("Processing nav-call-id: {}, response: {}", getHeaderFromRequestContext("nav-call-id"), response.toString())
        return ResponseEntity(response, HttpStatus.OK)
    }

    interface SimuleringService {
        fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest): SimulerOffentligTjenestepensjonResponse
    }

    fun addHeaderToRequestContext(key: String?, value: String?) {
        if (value != null) {
            RequestContextHolder.currentRequestAttributes().setAttribute(key, value, RequestAttributes.SCOPE_REQUEST)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SimuleringEndpoint::class.java)
    }

}