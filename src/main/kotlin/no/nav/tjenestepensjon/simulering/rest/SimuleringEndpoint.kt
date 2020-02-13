package no.nav.tjenestepensjon.simulering.rest

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT
import com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.service.SimuleringService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes
import java.time.LocalDate

@RestController
class SimuleringEndpoint(private val service: SimuleringService, private val metrics: AppMetrics) {

    class LocalDateEpochSerializer: JsonSerializer<LocalDate>(){
        override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(value.toString())
        }
    }

    @Bean
    fun objectMapper(): ObjectMapper{
        return ObjectMapper()
                .registerModule(KotlinModule())
                .registerModule(
                        JavaTimeModule()
                                .addSerializer(LocalDate::class.java, LocalDateEpochSerializer())
                )
    }

    @PostMapping("/simulering")
    fun simuler(@RequestBody request: SimulerPensjonRequest, @RequestHeader(value = NAV_CALL_ID, required = false) navCallId: String?): ResponseEntity<SimulerOffentligTjenestepensjonResponse> {
        addHeaderToRequestContext(NAV_CALL_ID, navCallId)
        LOG.info("Processing nav-call-id: {}, request: {}", getHeaderFromRequestContext(NAV_CALL_ID), request.toString())
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_CALLS)
        val response = service.simulerOffentligTjenestepensjon(request)
        LOG.info("Processing nav-call-id: {}, response: {}", getHeaderFromRequestContext(NAV_CALL_ID), response.toString())
        return ResponseEntity(response, OK)
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