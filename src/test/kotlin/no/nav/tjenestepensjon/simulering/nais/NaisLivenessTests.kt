package no.nav.tjenestepensjon.simulering.nais

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest(classes = [NaisLiveness::class])
@AutoConfigureMockMvc
class NaisLivenessTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `isAlive returns OK`() {
            mockMvc.perform(MockMvcRequestBuilders.get("/isAlive"))
                    .andExpect(MockMvcResultMatchers.status().isOk)
        }

    @Test
    fun `isReady returns OK`() {
            mockMvc.perform(MockMvcRequestBuilders.get("/isReady"))
                    .andExpect(MockMvcResultMatchers.status().isOk)
        }
}