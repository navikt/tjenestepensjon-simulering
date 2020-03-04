package no.nav.tjenestepensjon.simulering.v1.models.request

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v1.models.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTRequestMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test serializing of HentStillingsprosentListeRequest`() {
        val result = objectMapper.writeValueAsString(defaultHentStillingsprosentListeRequest)
        assertEquals(defaultHentStillingsprosentListeRequestJson, result)
    }

    @Test
    fun `Test serializing of SimulerPensjonsRequest`() {
        val result = objectMapper.writeValueAsString(defaultSimulerPensjonRequest)
        assertEquals(defaultSimulerPensjonRequestJson, result)
    }

    @Test
    fun `Test serializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonRequest)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestJson, result)
    }

    @Test
    fun `Test deserializing of HentStillingsprosentListeRequest`() {
        val result = objectMapper.readValue(defaultHentStillingsprosentListeRequestJson, HentStillingsprosentListeRequest::class.java)
        assertEquals(defaultHentStillingsprosentListeRequest, result)
    }

    @Test
    fun `Test deserializing of SimulerPensjonsRequest`() {
        val result = objectMapper.readValue(defaultSimulerPensjonRequestJson, SimulerPensjonRequest::class.java)
        assertEquals(defaultSimulerPensjonRequest, result)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonRequestJson, SimulerOffentligTjenestepensjonRequest::class.java)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequest, result)
    }
}