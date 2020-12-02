package no.nav.tjenestepensjon.simulering.v1.soap

import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.WebServiceMessageCallback
import org.springframework.ws.soap.SoapMessage
import org.springframework.ws.soap.addressing.client.ActionCallback
import org.springframework.ws.soap.addressing.version.Addressing10
import org.springframework.ws.soap.client.core.SoapActionCallback
import java.net.URI

class SOAPCallback(action: String, tpLeverandorUrl: String, samlToken: String, samlConfig: SamlConfig) : WebServiceMessageCallback {

    private val wsAddressingCallback = ActionCallback(URI(""), Addressing10(), URI(tpLeverandorUrl))
    private val soapActionCallback = SoapActionCallback(action)
    private val samlHeaderCallback = SamlHeaderCallback(samlToken, samlConfig)

    override fun doWithMessage(message: WebServiceMessage) {
        wsAddressingCallback.doWithMessage(message as SoapMessage)
        soapActionCallback.doWithMessage(message)
        samlHeaderCallback.doWithMessage(message)
    }
}