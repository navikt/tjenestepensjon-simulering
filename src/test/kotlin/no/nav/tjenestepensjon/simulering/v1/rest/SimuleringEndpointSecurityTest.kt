package no.nav.tjenestepensjon.simulering.v1.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
@AutoConfigureMockMvc
class SimuleringEndpointSecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var maskinportenTokenProvider: MaskinportenTokenProvider

    @Test
    fun insecureEndpointsAccessible() {
        mockMvc.get("/actuator/prometheus").andExpect { status { isOk() } }
        mockMvc.get("/actuator/health").andExpect { status { isOk() } }
        mockMvc.get("/actuator/health/liveness").andExpect { status { isOk() } }
        mockMvc.get("/actuator/health/readiness").andExpect { status { isOk() } }
    }

    private val body = """{
                |"fnr":"01011234567",
                |"sivilstandkode":"",
                |"inntekter":[],
                |"simuleringsperioder":[]
                |}""".trimMargin()

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

    @Test
    @WithMockUser
    fun secureEndpointOkWithValidToken() {
        `when`(maskinportenTokenProvider.generateTpregisteretToken()).thenReturn("")
        mockMvc.post("/simulering") {
            content = body
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    companion object {
        private var wireMockServer = WireMockServer().apply {
            start()
            stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/person/tpordninger"))
                    .willReturn(WireMock.okJson("""[{"tssId":"1234","tpId":"4321"}]"""))
            )
            stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/4321")).willReturn(WireMock.okJson("""{"KLP"}"""))
            )
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}