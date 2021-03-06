package no.nav.tjenestepensjon.simulering.v1.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import no.nav.tjenestepensjon.simulering.v1.exceptions.MissingStillingsprosentException
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class SimuleringEndpointSecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun insecureEndpointsAccessible() {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/prometheus")).andExpect(status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")).andExpect(status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/isAlive")).andExpect(status().isOk)
        mockMvc.perform(MockMvcRequestBuilders.get("/isReady")).andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointUnauthorizedWhenNoToken() {
        mockMvc.perform(MockMvcRequestBuilders.post("/simulering")
                .contentType(APPLICATION_JSON)
                .content("{}")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @Throws(Exception::class)
    fun secureEndpointUnauthorizedWhenInvalidToken() {
        mockMvc.perform(MockMvcRequestBuilders.post("/simulering")
                .contentType(APPLICATION_JSON)
                .content("{}")
                .header(AUTHORIZATION, "Bearer abc1234")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @Throws(MissingStillingsprosentException::class)
    fun secureEndpointOkWithValidToken() {
        mockMvc.perform(MockMvcRequestBuilders.post("/simulering")
                .contentType(APPLICATION_JSON)
                .content(
                    """{
                    |"fnr":"01011234567",
                    |"sivilstandkode":"",
                    |"inntekter":[],
                    |"simuleringsperioder":[]
                    |}""".trimMargin().replace("\n", ""))
                .header(AUTHORIZATION, "Bearer ${TokenProviderStub.accessToken}"))
    }

    companion object {
        private var wireMockServer = WireMockServer().apply {
            start()
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/person/tpordninger/intern"))
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