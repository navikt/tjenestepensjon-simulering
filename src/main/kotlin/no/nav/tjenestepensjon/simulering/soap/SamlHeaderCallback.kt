package no.nav.tjenestepensjon.simulering.soap

import no.nav.tjenestepensjon.simulering.config.ApplicationProperties.SAML_SECURITY_CONTEXT_URL
import org.springframework.util.Assert
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.WebServiceMessageCallback
import org.springframework.ws.soap.SoapMessage
import org.springframework.xml.transform.StringSource
import java.util.*
import javax.xml.namespace.QName
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory

class SamlHeaderCallback(private val token: String?) : WebServiceMessageCallback {
    private val transformer = try {
        TransformerFactory.newInstance().newTransformer()
    } catch (e: TransformerConfigurationException) {
        throw RuntimeException(e.message, e)
    }

    @Throws(TransformerException::class)
    override fun doWithMessage(message: WebServiceMessage) {
        Assert.isInstanceOf(SoapMessage::class.java, message)
        val soapMessage = message as SoapMessage
        transformer.transform(
                StringSource(String(Base64.getDecoder().decode(token))),
                soapMessage.soapHeader.addHeaderElement(QName(SAML_SECURITY_CONTEXT_URL, "Security")).result
        )
    }
}