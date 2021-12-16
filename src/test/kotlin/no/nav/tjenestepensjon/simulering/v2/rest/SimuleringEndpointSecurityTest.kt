package no.nav.tjenestepensjon.simulering.v2.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.ProxylessWebClientConfig
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenProvider
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerOffentligTjenestepensjonRequestJson
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class, ProxylessWebClientConfig::class])
@AutoConfigureMockMvc
@WebAppConfiguration
class SimuleringEndpointSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var maskinportenTokenProvider: MaskinportenTokenProvider

    @MockBean
    private lateinit var soapClient: SoapClient

    @MockBean
    private lateinit var restClient: RestClient

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
            content = defaultSimulerOffentligTjenestepensjonRequestJson
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun secureEndpointUnauthorizedWhenInvalidToken() {
        mockMvc.post("/simulering") {
            content = defaultSimulerOffentligTjenestepensjonRequestJson
            contentType = APPLICATION_JSON
            headers { setBearerAuth("abc1234") }
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser
    fun secureEndpointOkWithValidToken() {
        Mockito.`when`(maskinportenTokenProvider.generateTpregisteretToken()).thenReturn("")
        Mockito.`when`(soapClient.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull()))
            .thenReturn(defaultStillingsprosentListe)
        Mockito.`when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull()))
            .thenReturn(SimulerOffentligTjenestepensjonResponse("", ""))
        mockMvc.post("/simulering") {
            content = defaultSimulerOffentligTjenestepensjonRequestJson
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    companion object {
        private var wireMockServer = WireMockServer().apply {
            start()
            stubFor(
                get(urlPathEqualTo("/person/tpordninger")).willReturn(okJson("""[{"tssId":"1234","tpId":"4321"}]"""))
            )
            stubFor(
                get(urlPathEqualTo("/tpleverandoer/4321")).willReturn(okJson("""leverandor2"""))
            )
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}