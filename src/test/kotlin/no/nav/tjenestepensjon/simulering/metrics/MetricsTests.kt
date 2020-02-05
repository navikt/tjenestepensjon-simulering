package no.nav.tjenestepensjon.simulering.metrics

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class MetricsTests {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun actuator_exposes_health() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/actuator/health")).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun actuator_exposes_prometheus() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/actuator/prometheus")).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun actuator_does_not_expose_env_or_beans() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/actuator/env")).andExpect(MockMvcResultMatchers.status().isNotFound)
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/beans")).andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}