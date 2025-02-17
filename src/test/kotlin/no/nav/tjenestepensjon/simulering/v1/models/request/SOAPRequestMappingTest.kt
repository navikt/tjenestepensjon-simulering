package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v1.models.defaultHentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.models.defaultHentStillingsprosentListeRequestXML
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.xml.transform.StringSource
import java.io.StringWriter
import java.time.ZoneOffset
import java.util.*
import javax.xml.transform.stream.StreamResult
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class SOAPRequestMappingTest {

    init {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.of("+1")))
    }

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
    fun `Test unmarshalling of HentStillingsprosentListeRequest`(){
        val wrapper = marshaller.unmarshal(StringSource(defaultHentStillingsprosentListeRequestXML))
        val castWrapper = wrapper as XMLHentStillingsprosentListeRequestWrapper
        val output = SOAPAdapter.unmarshal(castWrapper)
        assertEquals(defaultHentStillingsprosentListeRequest, output)
    }

}