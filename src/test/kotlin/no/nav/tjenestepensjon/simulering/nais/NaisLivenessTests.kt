package no.nav.tjenestepensjon.simulering.nais

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjonimport
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

org.junit.jupiter.api.Testimport java.lang.Exception

@SpringBootTest
@AutoConfigureMockMvc
class NaisLivenessTests {
    @Autowired
    private val mockMvc: MockMvc? = null

    @get:Throws(Exception::class)
    @get:Test
    val isAlive_returns_OK: Unit
        get() {
            mockMvc!!.perform(MockMvcRequestBuilders.get("/isAlive")).andExpect(MockMvcResultMatchers.status().isOk)
        }

    @get:Throws(Exception::class)
    @get:Test
    val isReady_returns_OK: Unit
        get() {
            mockMvc!!.perform(MockMvcRequestBuilders.get("/isReady")).andExpect(MockMvcResultMatchers.status().isOk)
        }
}