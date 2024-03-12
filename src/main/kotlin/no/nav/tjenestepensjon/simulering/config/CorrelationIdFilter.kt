package no.nav.tjenestepensjon.simulering.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

@Component
class CorrelationIdFilter : Filter {
    companion object {
        const val CORRELATION_ID_HTTP_HEADER = "Nav-Call-Id"
        const val CONSUMER_ID_HTTP_HEADER = "Nav-Consumer-Id"
        const val CORRELATION_ID = "transaction"
        const val CONSUMER_ID = "user"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val correlationId = httpRequest.getHeader(CORRELATION_ID_HTTP_HEADER) ?: UUID.randomUUID().toString()
        val consumerId = httpRequest.getHeader(CONSUMER_ID_HTTP_HEADER) ?: "unknown_consumer"
        MDC.put(CORRELATION_ID, correlationId)
        MDC.put(CONSUMER_ID, consumerId)
        try {
            chain.doFilter(request, response)
        } finally {
            MDC.remove(CORRELATION_ID)
            MDC.remove(CONSUMER_ID)
        }
    }
}