package no.nav.tjenestepensjon.simulering.v1.soap

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.support.interceptor.ClientInterceptor
import org.springframework.ws.context.MessageContext
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.dom.DOMSource

class NorwegianSoapResponseInterceptor: ClientInterceptor {
    private val logger = KotlinLogging.logger {}
    private val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
    override fun handleRequest(messageContext: MessageContext) = true

    override fun handleResponse(messageContext: MessageContext): Boolean {
        reformatPayload(messageContext.response)
        return true
    }

    override fun handleFault(messageContext: MessageContext): Boolean {
        reformatPayload(messageContext.response)
        return true
    }

    override fun afterCompletion(messageContext: MessageContext, ex: Exception?) {
    }

    private fun reformatPayload(message: WebServiceMessage) {
        try {
            val domResult = DOMResult()
            transformer.transform(message.payloadSource, domResult)

            val body = domResult.node?.apply {

                nodeValue = nodeValue
                    .replace("Ø", "Oe").replace("ø", "oe")
                    .replace("Å", "Aa").replace("å", "aa")
                    .replace("Æ", "Ae").replace("æ", "ae")
            }

            if (body != null) {
                val newMessageSource = DOMSource(body)
                transformer.transform(newMessageSource, message.payloadResult)
            }
        } catch (e: Throwable) {
            logger.error(e) { "Error reformatting payload ${e.message}" }
        }
    }
}