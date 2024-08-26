package no.nav.tjenestepensjon.simulering.v1.soap

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import org.springframework.ws.client.support.interceptor.ClientInterceptor
import org.springframework.ws.context.MessageContext
import org.springframework.ws.transport.context.TransportContextHolder
import org.springframework.ws.transport.http.HttpUrlConnection

class AuthAttachingHttpRequestInterceptor(private val fssGatewayAuthService: FssGatewayAuthService) : ClientInterceptor {
    private val logger = KotlinLogging.logger {}

    override fun handleRequest(messageContext: MessageContext): Boolean {
        fssGatewayAuthService.hentToken()?.let {
            val transportContext = TransportContextHolder.getTransportContext()
            val connection = transportContext.connection as HttpUrlConnection
            connection.connection.addRequestProperty("Authorization", "Bearer $it")
            logger.info { "Attaching token to SOAP request" }
        }
        return true
    }

    override fun handleResponse(messageContext: MessageContext): Boolean {
        return true
    }

    override fun handleFault(messageContext: MessageContext): Boolean {
        return true
    }

    override fun afterCompletion(messageContext: MessageContext, ex: Exception?) {
    }
}