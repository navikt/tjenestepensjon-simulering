package no.nav.tjenestepensjon.simulering.v2.models.response

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v2.models.defaultOppjeningsperiodeListeJson
import no.nav.tjenestepensjon.simulering.v2.models.defaultOpptjeningsperiodeListe
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerOffentligTjenestepensjonResponseJson
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
        val result = objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonResponse)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponseJson, result)
    }

    @Test
    fun `Test serializing SimulerOffentligTjenestepensjonResponse`() {
        val result = objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonResponse)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponseJson, result)
    }

    @Test
    fun `Test serializing HentOpptjeningsperiodeListeResponse`() {
        val result = objectMapper.writeValueAsString(defaultOpptjeningsperiodeListe)
        assertEquals(defaultOppjeningsperiodeListeJson, result)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonResponse OK`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonResponseJson, SimulerOffentligTjenestepensjonResponse::class.java)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponse, result)
    }

    @Test
    fun `Test deserializing SimulerOffentligTjenestepensjonResponse`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonResponseJson, SimulerOffentligTjenestepensjonResponse::class.java)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponse, result)
    }
}