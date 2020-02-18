package no.nav.tjenestepensjon.simulering.model.v1.request

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.model.v1.defaultHentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.model.v1.defaultHentStillingsprosentListeRequestXML
import no.nav.tjenestepensjon.simulering.model.v1.defaultSimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.defaultSimulerOffentligTjenestepensjonRequestXML
import no.nav.tjenestepensjon.simulering.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.xml.transform.StringSource
import java.io.StringWriter
import javax.xml.transform.stream.StreamResult
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class SOAPRequestMappingTest {

    @Autowired
    lateinit var marshaller: Jaxb2Marshaller

    lateinit var writer: StringWriter
    lateinit var result: StreamResult

    @BeforeEach
    fun reset(){
        writer = StringWriter()
        result = StreamResult(writer)
    }

    @Test
    fun `Test marshalling of HentStillingsprosentListeRequest`(){
        marshaller.marshal(
                SOAPAdapter.marshal(defaultHentStillingsprosentListeRequest),
                result
        )
        val output = writer.toString()
        assertEquals(defaultHentStillingsprosentListeRequestXML, output)
    }

    @Test
    fun `Test marshalling of SimulerOffentligTjenestepensjonRequest`(){
        marshaller.marshal(
                SOAPAdapter.marshal(defaultSimulerOffentligTjenestepensjonRequest),
                result
        )
        val output = writer.toString()
        println("result  :$output")
        println("expected:$defaultSimulerOffentligTjenestepensjonRequestXML")
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestXML, output)
    }

    @Test
    fun `Test unmarshalling of HentStillingsprosentListeRequest`(){
        val wrapper = marshaller.unmarshal(StringSource(defaultHentStillingsprosentListeRequestXML))
        val castWrapper = wrapper as XMLHentStillingsprosentListeRequestWrapper
        val output = SOAPAdapter.unmarshal(castWrapper)
        assertEquals(defaultHentStillingsprosentListeRequest, output)
    }

    @Test
    fun `Test unmarshalling of SimulerOffentligTjenestepensjonRequest`(){
        val wrapper = marshaller.unmarshal(StringSource(defaultSimulerOffentligTjenestepensjonRequestXML)) as XMLSimulerOffentligTjenestepensjonRequestWrapper
        val output = SOAPAdapter.unmarshal(wrapper)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequest, output)
    }
}