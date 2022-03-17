package no.nav.tjenestepensjon.simulering.v2.models.response

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
    lateinit var jsonMapper: JsonMapper

    @Test
    fun `Test serializing of SimulertPensjon OK`() {
        val result = jsonMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonResponse)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponseJson, result)
    }

    @Test
    fun `Test serializing SimulerOffentligTjenestepensjonResponse`() {
        val result = jsonMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonResponse)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponseJson, result)
    }

    @Test
    fun `Test serializing HentOpptjeningsperiodeListeResponse`() {
        val result = jsonMapper.writeValueAsString(defaultOpptjeningsperiodeListe)
        assertEquals(defaultOppjeningsperiodeListeJson, result)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonResponse OK`() {
        val result = jsonMapper.readValue<SimulerOffentligTjenestepensjonResponse>(defaultSimulerOffentligTjenestepensjonResponseJson)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponse, result)
    }

    @Test
    fun `Test deserializing SimulerOffentligTjenestepensjonResponse`() {
        val result = jsonMapper.readValue<SimulerOffentligTjenestepensjonResponse>(defaultSimulerOffentligTjenestepensjonResponseJson)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponse, result)
    }
}
