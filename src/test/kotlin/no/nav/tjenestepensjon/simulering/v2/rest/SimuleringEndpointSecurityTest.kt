package no.nav.tjenestepensjon.simulering.v2.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.*
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import no.nav.tjenestepensjon.simulering.v2.models.defaultLeverandor
import no.nav.tjenestepensjon.simulering.v2.models.defaultSimulerOffentligTjenestepensjonRequestJson
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
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

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
class SimuleringEndpointSecurityTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var aadClient: AADClient

    @MockBean
    private lateinit var soapClient: SoapClient

    @MockBean
    private lateinit var restClient: RestClient

    private var wireMockServer = WireMockServer().apply {
        start()
        stubFor(defaultTjenestepensjonRequest.willReturn(okJson(defaultForhold)))
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
        `when`(aadClient.getToken("api://bogus")).thenReturn("")
        `when`(soapClient.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(
            defaultStillingsprosentListe
        )
        `when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(
            SimulerOffentligTjenestepensjonResponse("", "")
        )
        mockMvc.post("/simulering") {
            content = defaultSimulerOffentligTjenestepensjonRequestJson
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

}
