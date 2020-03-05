package no.nav.tjenestepensjon.simulering.v2.models.response

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v2.models.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTResponseMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test serializing of SimulertPensjon OK`() {
        val result = objectMapper.writeValueAsString(defaultSimulertPensjon)
        assertEquals(defaultSimulertPensjonJson, result)
    }

    @Test
    fun `Test serializing SimulerOffentligTjenestepensjonResponse`() {
        val result = objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonResponse)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponseJson, result)
    }

    @Test
    fun `Test serializing HentStillingsprosentListeResponse`() {
        val result = objectMapper.writeValueAsString(defaultHentStillingsprosentListeResponse)
        assertEquals(defaultHentStillingsprosentListeResponseJson, result)
    }

    @Test
    fun `Test deserializing of SimulertPensjon OK`() {
        val result = objectMapper.readValue(defaultSimulertPensjonJson, SimulertPensjon::class.java)
        assertEquals(defaultSimulertPensjon, result)
    }

    @Test
    fun `Test deserializing SimulerOffentligTjenestepensjonResponse`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonResponseJson, SimulerOffentligTjenestepensjonResponse::class.java)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponse, result)
    }

    @Test
    fun `Test deserializing HentStillingsprosentListeResponse`() {
        val result = objectMapper.readValue(defaultHentStillingsprosentListeResponseJson, HentStillingsprosentListeResponse::class.java)
        assertEquals(defaultHentStillingsprosentListeResponse, result)
    }
}