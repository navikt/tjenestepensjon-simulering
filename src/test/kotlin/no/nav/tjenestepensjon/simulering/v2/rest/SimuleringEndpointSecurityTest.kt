package no.nav.tjenestepensjon.simulering.v2.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
class SimuleringEndpointSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val body = """{
                |"fnr":"01011234567",
                |"sivilstandkode":"",
                |"inntekter":[],
                |"simuleringsperioder":[]
                |}""".trimMargin()

    @Test
    fun insecureEndpointsAccessible() {
        mockMvc.get("/actuator/prometheus").andExpect { status { isOk() } }
        mockMvc.get("/actuator/health").andExpect { status { isOk() } }
        mockMvc.get("/actuator/health/liveness").andExpect { status { isOk() } }
        mockMvc.get("/actuator/health/readiness").andExpect { status { isOk() } }
    }

    @Test
    fun secureEndpointUnauthorizedWhenNoToken() {
        mockMvc.post("/simulering") {
            content = body
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun secureEndpointUnauthorizedWhenInvalidToken() {
        mockMvc.post("/simulering") {
            content = body
            contentType = APPLICATION_JSON
            headers { setBearerAuth("abc1234") }
        }.andExpect {
            status { isUnauthorized() }
        }
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
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/person/tpordninger/intern"))
                    .willReturn(WireMock.okJson("""[{"tssId":"1234","tpId":"4321"}]""")))
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/4321"))
                    .willReturn(WireMock.okJson("""{"KLP"}""")))
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}