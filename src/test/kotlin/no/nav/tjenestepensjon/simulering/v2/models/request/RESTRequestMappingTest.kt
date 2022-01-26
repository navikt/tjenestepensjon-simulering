package no.nav.tjenestepensjon.simulering.v2.models.request

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v2.models.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTRequestMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test serializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.writeValueAsString(defaultSimulertPensjonRequest)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestJson, result)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.readValue(
            defaultSimulerOffentligTjenestepensjonRequestJson, SimulerPensjonRequestV2::class.java
        )
        assertEquals(
            objectMapper.writeValueAsString(defaultSimulertPensjonRequest), objectMapper.writeValueAsString(result)
        )
    }
}
