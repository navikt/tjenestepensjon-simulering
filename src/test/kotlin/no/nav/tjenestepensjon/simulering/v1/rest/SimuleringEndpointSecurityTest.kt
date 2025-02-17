package no.nav.tjenestepensjon.simulering.v1.rest

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.*
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.service.TokenService
import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import no.nav.tjenestepensjon.simulering.v1.models.defaultLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.defaultSimulerPensjonRequestJson
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@TestPropertySource(locations = ["classpath:application.yml"])
class SimuleringEndpointSecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var aadClient: AADClient

    @MockitoBean
    private lateinit var fssGatewayAuthService: FssGatewayAuthService

    @MockitoBean
    private lateinit var tokenService: TokenService

    @MockitoBean
    private lateinit var soapClient: SoapClient

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
            content = defaultSimulerPensjonRequestJson
            contentType = APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun secureEndpointUnauthorizedWhenInvalidToken() {
        mockMvc.post("/simulering") {
            content = defaultSimulerPensjonRequestJson
            contentType = APPLICATION_JSON
            headers { setBearerAuth("abc1234") }
        }.andExpect {
            status { isUnauthorized() }
        }
    }

}
