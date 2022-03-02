package no.nav.tjenestepensjon.simulering.v2.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.tjenestepensjon.simulering.*
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenProvider
import no.nav.tjenestepensjon.simulering.v2.models.defaultLeverandor
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerOffentligTjenestepensjonRequestJson
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
class SimuleringEndpointSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var maskinportenTokenProvider: MaskinportenTokenProvider

    @MockkBean
    private lateinit var aadClient: AADClient

    @MockkBean
    private lateinit var soapClient: SoapClient

    @MockkBean
    private lateinit var restClient: RestClient

    private var wireMockServer = WireMockServer().apply {
        start()
        stubFor(get(urlPathEqualTo(defaultTjenestepensjonUrl)).willReturn(okJson(defaultTjenestepensjon)))
        stubFor(get(urlPathEqualTo(defaultLeveradorUrl)).willReturn(okJson(defaultLeverandor)))
        stubFor(get(urlPathEqualTo(defaultTssnrUrl)).willReturn(okJson(defaultTssid)))
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

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
        every { aadClient.getToken("api://bogus") } returns ""
        every { soapClient.getStillingsprosenter(any(), any(), any()) } returns defaultStillingsprosentListe
        every { restClient.getResponse(any(), any(), any()) } returns SimulerOffentligTjenestepensjonResponse("", "")

        mockMvc.post("/simulering") {
            content = defaultSimulerOffentligTjenestepensjonRequestJson
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

}
