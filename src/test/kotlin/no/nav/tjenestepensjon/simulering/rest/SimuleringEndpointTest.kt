package no.nav.tjenestepensjon.simulering.rest

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjonimport
import com.github.tomakehurst.wiremock.WireMockServer
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

org.junit.jupiter.api.Testimport org.springframework.http.HttpHeadersimport java.lang.Exception

@SpringBootTest
@AutoConfigureMockMvc
class SimuleringEndpointTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @Test
    @Throws(Exception::class)
    fun endepunkt_kalles_uten_requestbody() {
        mockMvc!!.perform(MockMvcRequestBuilders.post("/simulering").header(HttpHeaders.AUTHORIZATION, "Bearer " + TokenProviderStub.getAccessToken())).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    companion object {
        private var wireMockServer: WireMockServer? = null
        @BeforeAll
        fun beforeAll() {
            wireMockServer = WireMockServer()
            wireMockServer!!.start()
            TokenProviderStub.configureTokenProviderStub(wireMockServer)
        }

        //    @Test
//    public void simulering_returns_OK() throws Exception {
//        mockMvc.perform(get("/simulering").contentType(MediaType.APPLICATION_JSON).content("{ \"fnr\": \"lol\", \"inntekter\": [{ \"inntekt\": 101 }]}"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{ \"simulertPensjonListe\" : null} "));
//    }
        @AfterAll
        fun afterAll() {
            wireMockServer!!.stop()
        }
    }
}