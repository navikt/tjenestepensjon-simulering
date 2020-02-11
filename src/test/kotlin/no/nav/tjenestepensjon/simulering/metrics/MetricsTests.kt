package no.nav.tjenestepensjon.simulering.metrics

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class MetricsTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Actuator exposes health`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Actuator exposes prometheus`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/prometheus"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Actuator does not expose env or beans`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/env"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/beans"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}