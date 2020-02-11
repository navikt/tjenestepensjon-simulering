package no.nav.tjenestepensjon.simulering.soap

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.WebServiceMessageCallback
import org.springframework.ws.soap.SoapMessage
import org.springframework.ws.soap.addressing.client.ActionCallback
import org.springframework.ws.soap.addressing.version.Addressing10
import org.springframework.ws.soap.client.core.SoapActionCallback
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import javax.xml.transform.TransformerException

@Component
class SOAPCallback(action: String, tpLeverandorUrl: String, samlToken: String?, samlConfig: SamlConfig) : WebServiceMessageCallback {
    private val wsAddressingCallback = try {
        ActionCallback(URI(""), Addressing10(), URI(tpLeverandorUrl))
    } catch (e: URISyntaxException) {
        throw RuntimeException(e.message, e)
    }
    private val soapActionCallback: SoapActionCallback = SoapActionCallback(action)

    @Autowired
    private val samlHeaderCallback: SamlHeaderCallback = SamlHeaderCallback(samlToken, samlConfig)

    @Throws(IOException::class, TransformerException::class)
    override fun doWithMessage(message: WebServiceMessage) {
        Assert.isInstanceOf(SoapMessage::class.java, message)
        wsAddressingCallback.doWithMessage(message)
        soapActionCallback.doWithMessage(message)
        samlHeaderCallback.doWithMessage(message)
    }
}