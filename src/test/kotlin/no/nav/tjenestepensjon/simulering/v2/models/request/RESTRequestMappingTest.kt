package no.nav.tjenestepensjon.simulering.v2.models.request

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v2.models.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTRequestMappingTest {

    @Autowired
    lateinit var jsonMapper: JsonMapper

    @Test
    fun `Test serializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = jsonMapper.writeValueAsString(defaultSimulertPensjonRequest)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestJson, result)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = jsonMapper.readValue<SimulerPensjonRequestV2>(defaultSimulerOffentligTjenestepensjonRequestJson)
        assertEquals(
            jsonMapper.writeValueAsString(defaultSimulertPensjonRequest), jsonMapper.writeValueAsString(result)
        )
    }
}
