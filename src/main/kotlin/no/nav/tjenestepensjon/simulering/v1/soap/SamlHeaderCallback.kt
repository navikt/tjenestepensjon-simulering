package no.nav.tjenestepensjon.simulering.v1.soap

import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.WebServiceMessageCallback
import org.springframework.ws.soap.SoapMessage
import org.springframework.xml.transform.StringSource
import java.util.*
import javax.xml.namespace.QName
import javax.xml.transform.TransformerFactory

class SamlHeaderCallback(private val token: String, private val samlConfig: SamlConfig) : WebServiceMessageCallback {

    private val transformer = TransformerFactory.newInstance().newTransformer()

    override fun doWithMessage(message: WebServiceMessage) {
        val soapMessage = message as SoapMessage

        // Safely add header without affecting Content-Length
        val headerElement = soapMessage.soapHeader.addHeaderElement(
            QName(samlConfig.samlSecurityContextUrl, "Security")
        )

        // Use the transformer to ensure message integrity
        transformer.transform(StringSource(decodeToken(token)), headerElement.result)
    }
    companion object {
        fun decodeToken(token: String): String {
            return if (token.contains("-")) String(Base64.getUrlDecoder().decode(token))
            else String(Base64.getDecoder().decode(token))
        }
    }
}