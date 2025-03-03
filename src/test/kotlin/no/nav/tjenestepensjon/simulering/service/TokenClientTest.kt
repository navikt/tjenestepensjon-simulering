package no.nav.tjenestepensjon.simulering.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import no.nav.tjenestepensjon.simulering.v1.consumer.GatewayTokenClient
import no.nav.tjenestepensjon.simulering.v1.consumer.GatewayTokenClient.Companion.TOKEN_EXCHANGE_PATH
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.web.reactive.function.client.WebClient

@TestInstance(PER_CLASS)
internal class GatewayTokenClientTest {
    private val gatewayFssService = Mockito.mock(FssGatewayAuthService::class.java)
    private val gatewayTokenClient = GatewayTokenClient(WebClient.builder().baseUrl("http://localhost:8080").build(), gatewayFssService)

    private var wireMockServer = WireMockServer(8080).apply {
        start()
        stubFor(
            post(urlPathEqualTo("/rest/v1/sts/token/exchange")).willReturn(okJson("""{"access_token":"eyJ4vaea3","expires_in":"3600","token_type":"Bearer","issued_token_type":"saml:blah:2.0"}"""))
        )
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `Should get saml token`() {
        `when`(gatewayFssService.hentToken()).thenReturn("eyJ4vaea3")
        gatewayTokenClient.samlAccessToken.apply {
            assertEquals("eyJ4vaea3", accessToken)
            assertEquals(3600L, expiresIn)
            assertEquals("Bearer", tokenType)
            assertEquals("saml:blah:2.0", issuedTokenType)
        }
    }
}
