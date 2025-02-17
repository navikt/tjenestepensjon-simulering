package no.nav.tjenestepensjon.simulering.v1.models.response

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v1.models.defaultHentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.models.defaultHentStillingsprosentListeResponseXML
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
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
class SOAPResponseMappingTest {

    init {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.of("+1")))
    }

    @Autowired
    lateinit var marshaller: Jaxb2Marshaller

    lateinit var writer: StringWriter
    lateinit var result: StreamResult

    @BeforeEach
    fun reset() {
        writer = StringWriter()
        result = StreamResult(writer)
    }

    @Test
    fun `Test marshalling of HentStillingsprosentListeResponse`() {
        marshaller.marshal(
            SOAPAdapter.marshal(defaultHentStillingsprosentListeResponse), result
        )
        val output = writer.toString()
        assertEquals(defaultHentStillingsprosentListeResponseXML, output)
    }

    @Test
    fun `Test unmarshalling of HentStillingsprosentListeResponse`() {
        val wrapper =
            marshaller.unmarshal(StringSource(defaultHentStillingsprosentListeResponseXML)) as XMLHentStillingsprosentListeResponseWrapper
        val output = SOAPAdapter.unmarshal(wrapper)
        assertEquals(defaultHentStillingsprosentListeResponse, output)
    }
}
