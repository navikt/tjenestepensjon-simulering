package no.nav.tjenestepensjon.simulering.rest

import com.github.tomakehurst.wiremock.WireMockServer
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class SimuleringEndpointTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun secureEndpointOkWithValidToken() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/simulering")
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    companion object {
        private var wireMockServer = WireMockServer()
                .apply { start() }
                .also(TokenProviderStub::configureTokenProviderStub)

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}