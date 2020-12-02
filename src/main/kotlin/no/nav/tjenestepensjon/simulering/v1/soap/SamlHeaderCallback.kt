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
        transformer.transform(
                StringSource(String(Base64.getDecoder().decode(token))),
                (message as SoapMessage).soapHeader.addHeaderElement(
                        QName(samlConfig.samlSecurityContextUrl, "Security")
                ).result
        )
    }
}