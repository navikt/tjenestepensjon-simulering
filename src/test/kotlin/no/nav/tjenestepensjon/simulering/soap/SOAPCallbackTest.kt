package no.nav.tjenestepensjon.simulering.soap

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjonimport
import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.ws.soap.SoapMessage
import org.springframework.ws.soap.saaj.SaajSoapMessage
import org.springframework.ws.transport.context.DefaultTransportContext
import org.springframework.ws.transport.context.TransportContextHolder
import org.springframework.ws.transport.http.HttpUrlConnection
import java.io.IOException
import javax.xml.namespace.QName
import javax.xml.transform.TransformerException

org.hamcrest.Matchersimport org.junit.jupiter.api.Testimport java.util.*import javax.xml.namespace.QName

@ExtendWith(MockitoExtension::class)
internal class SOAPCallbackTest {
    private val token = "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"></saml2:Assertion>\n"
    private val callback: SOAPCallback = SOAPCallback("soapAction", "tpLeverandorUrl", String(Base64.getEncoder().encode(token.toByteArray())))
    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldAddWsAddressingTo() {
        val wsAddressingQname = "{http://www.w3.org/2005/08/addressing}To"
        val message: SoapMessage = SaajSoapMessage(Message1_1Impl())
        MatcherAssert.assertThat(message.soapHeader.examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext(), Matchers.`is`(false))
        callback.doWithMessage(message)
        MatcherAssert.assertThat(message.soapHeader.examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext(), Matchers.`is`(true))
        MatcherAssert.assertThat<String>(message.soapHeader.examineHeaderElements(QName.valueOf(wsAddressingQname)).next().text, Matchers.`is`("tpLeverandorUrl"))
    }

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldNotAddWsAddressingAction() {
        val addressActionQname = "{http://www.w3.org/2005/08/addressing}Action"
        val message: SoapMessage = SaajSoapMessage(Message1_1Impl())
        MatcherAssert.assertThat(message.soapHeader.examineHeaderElements(QName.valueOf(addressActionQname)).hasNext(), Matchers.`is`(false))
        callback.doWithMessage(message)
        MatcherAssert.assertThat(message.soapHeader.examineHeaderElements(QName.valueOf(addressActionQname)).hasNext(), Matchers.`is`(true))
        MatcherAssert.assertThat<String>(message.soapHeader.examineHeaderElements(QName.valueOf(addressActionQname)).next().text, Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldAddSoapActionToMessage() {
        val message: SoapMessage = SaajSoapMessage(Message1_1Impl())
        callback.doWithMessage(message)
        MatcherAssert.assertThat<String>(message.soapAction, Matchers.`is`("\"soapAction\""))
    }

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldAddSamlTokenHeader() {
        val securityHeaderQname = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security"
        val message: SoapMessage = SaajSoapMessage(Message1_1Impl())
        MatcherAssert.assertThat(message.soapHeader.examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext(), Matchers.`is`(false))
        callback.doWithMessage(message)
        MatcherAssert.assertThat(message.soapHeader.examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext(), Matchers.`is`(true))
    }

    companion object {
        @BeforeAll
        fun beforeAll() {
            val defaultTransportContext = Mockito.mock(DefaultTransportContext::class.java)
            Mockito.`when`(defaultTransportContext.connection).thenReturn(Mockito.mock(HttpUrlConnection::class.java))
            TransportContextHolder.setTransportContext(defaultTransportContext)
        }
    }
}