package no.nav.tjenestepensjon.simulering.v1.soap

import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.ws.soap.saaj.SaajSoapMessage
import org.springframework.ws.transport.context.DefaultTransportContext
import org.springframework.ws.transport.context.TransportContextHolder
import org.springframework.ws.transport.http.HttpUrlConnection
import java.io.IOException
import java.util.*
import javax.xml.namespace.QName
import javax.xml.transform.TransformerException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [SamlConfig::class])
@ExtendWith(MockKExtension::class)
internal class SOAPCallbackTest {

    @MockK
    lateinit var defaultTransportContext: DefaultTransportContext

    @Autowired
    lateinit var samlConfig: SamlConfig

    lateinit var callback: SOAPCallback
    lateinit var message: SaajSoapMessage

    @BeforeAll
    fun setup() {
        callback = SOAPCallback(
                "soapAction",
                tpLeverandorUrl,
                Base64.getEncoder().encodeToString(token.toByteArray()),
                samlConfig
        )

        every { defaultTransportContext.connection } returns mockk<HttpUrlConnection>()
        TransportContextHolder.setTransportContext(defaultTransportContext)
    }

    @BeforeEach
    fun reset() {
        message = SaajSoapMessage(Message1_1Impl())
    }

    fun execute() = callback.doWithMessage(message)

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldAddWsAddressingTo() {

        assertFalse(message.soapHeader.examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext())

        execute()

        assertTrue(message.soapHeader.examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext())
        assertEquals(tpLeverandorUrl, message.soapHeader.examineHeaderElements(QName.valueOf(wsAddressingQname)).next().text)
    }

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldNotAddWsAddressingAction() {
        assertFalse(message.soapHeader.examineHeaderElements(QName.valueOf(addressActionQname)).hasNext())
        execute()
        assertTrue(message.soapHeader.examineHeaderElements(QName.valueOf(addressActionQname)).hasNext())
        assertNull(message.soapHeader.examineHeaderElements(QName.valueOf(addressActionQname)).next().text)
    }

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldAddSoapActionToMessage() {
        execute()
        assertEquals(""""soapAction"""", message.soapAction)
    }

    @Test
    @Throws(IOException::class, TransformerException::class)
    fun shouldAddSamlTokenHeader() {
        assertFalse(message.soapHeader.examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext())
        execute()
        assertTrue(message.soapHeader.examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext())
    }

    companion object {
        private const val tpLeverandorUrl = "tpLeverandorUrl"
        private const val wsAddressingQname = "{http://www.w3.org/2005/08/addressing}To"
        private const val addressActionQname = "{http://www.w3.org/2005/08/addressing}Action"
        private const val securityHeaderQname = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security"
        private const val token = """<saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion"></saml2:Assertion>"""
    }
}
