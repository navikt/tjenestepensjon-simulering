package no.nav.tjenestepensjon.simulering.metrics

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class MetricsTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Actuator exposes health`() {
        mockMvc.get("/actuator/health").andExpect { status { isOk() } }
    }

    @Test
    fun `Actuator exposes prometheus`() {
        mockMvc.get("/actuator/prometheus").andExpect { status { isOk() } }
    }

    @Test
    fun `Actuator does not expose env or beans`() {
        mockMvc.get("/actuator/env").andExpect { status { isNotFound() } }
        mockMvc.get("/actuator/beans").andExpect { status { isNotFound() } }
    }
}
