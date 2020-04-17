package no.nav.tjenestepensjon.simulering.v2.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class SimuleringEndpointSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun insecureEndpointsAccessible() {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/prometheus")).andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")).andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/isAlive")).andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/isReady")).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointUnauthorizedWhenNoToken() {
        mockMvc.perform(MockMvcRequestBuilders.post("/simulering")
                .contentType(APPLICATION_JSON)
                .content("{}")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointUnauthorizedWhenInvalidToken() {
        mockMvc.perform(MockMvcRequestBuilders.post("/simulering")
                .contentType(APPLICATION_JSON)
                .content("{}")
                .header(AUTHORIZATION, "Bearer abc1234")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }


//    @Test
//    @Throws(Exception::class)
//    fun secureEndpointOkWithValidToken() {
//        val result = mockMvc.perform(MockMvcRequestBuilders.post("/simulering")
//                .contentType(APPLICATION_JSON)
//                .content(
//                    defaultSimulerOffentligTjenestepensjonRequestJson
//                            .trimMargin().replace("\n", ""))
//                .header(AUTHORIZATION, "Bearer ${TokenProviderStub.accessToken}")
//        ).andReturn()
//
//        Assertions.assertEquals(result.response.contentAsString, "Could not get opptjeningsperiode from any TP-Providers")
//    }

    companion object {
        private var wireMockServer = WireMockServer().apply {
            start()
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/person/tpordninger"))
                    .willReturn(WireMock.okJson("""[{"tssId":"1234","tpId":"4321"}]""")))
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/4321"))
                    .willReturn(WireMock.okJson("""{"KLP"}""")))
            TokenProviderStub.configureTokenProviderStub(this)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}