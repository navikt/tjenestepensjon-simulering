package no.nav.tjenestepensjon.simulering.rest

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class SimuleringEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser
    fun secureEndpointOkWithValidToken() {
        mockMvc.post("/simulering").andExpect {
            status { isBadRequest() }
        }
    }
}