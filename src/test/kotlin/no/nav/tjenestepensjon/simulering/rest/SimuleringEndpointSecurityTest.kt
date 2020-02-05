package no.nav.tjenestepensjon.simulering.rest

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjonimport
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

org.junit.jupiter.api.Testimport org.springframework.http.HttpHeadersimport org.springframework.http.MediaTypeimport java.lang.Exception

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class SimuleringEndpointSecurityTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun insecureEndpointsAccessible() {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/actuator/prometheus")).andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")).andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/isAlive")).andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/isReady")).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointUnauthorizedWhenNoToken() {
        mockMvc!!.perform(MockMvcRequestBuilders.post("/simulering").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointUnauthorizedWhenInvalidToken() {
        mockMvc!!.perform(MockMvcRequestBuilders.post("/simulering").contentType(MediaType.APPLICATION_JSON).content("{}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer abc1234"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointOkWithValidToken() {
        mockMvc!!.perform(MockMvcRequestBuilders.post("/simulering").contentType(MediaType.APPLICATION_JSON).content("{}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenProviderStub.getAccessToken()))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    companion object {
        private var wireMockServer: WireMockServer? = null
        @BeforeAll
        fun beforeAll() {
            wireMockServer = WireMockServer()
            wireMockServer!!.start()
            wireMockServer!!.stubFor(WireMock.get(WireMock.urlPathEqualTo("/person/null/tpordninger"))
                    .willReturn(WireMock.okJson("[{\"tssId\":\"1234\",\"tpId\":\"4321\"}]")))
            wireMockServer!!.stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/4321"))
                    .willReturn(WireMock.okJson("{\"KLP\"}")))
            TokenProviderStub.configureTokenProviderStub(wireMockServer)
        }

        @AfterAll
        fun afterAll() {
            wireMockServer!!.stop()
        }
    }
}