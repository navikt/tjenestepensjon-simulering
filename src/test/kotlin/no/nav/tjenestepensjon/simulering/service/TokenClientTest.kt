package no.nav.tjenestepensjon.simulering.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.tjenestepensjon.simulering.domain.Token
import no.nav.tjenestepensjon.simulering.v1.consumer.TokenClientOld
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.springframework.web.reactive.function.client.WebClient

@TestInstance(PER_CLASS)
internal class TokenClientTest {
    private val tokenClientOld: TokenClientOld = TokenClientOld(WebClient.create()).apply {
        username = "username"
        password = "password"
        stsUrl = "http://localhost:8080"
    }

    private var wireMockServer = WireMockServer().apply { start() }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `Should get saml token`() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/samltoken"))
                .willReturn(WireMock.okJson("""{"access_token":"eyJ4vaea3","expires_in":"3600","token_type":"Bearer","issued_token_type":"saml:blah:2.0"}"""))
        )
        val token: Token = tokenClientOld.samlAccessToken
        assertEquals("eyJ4vaea3", token.accessToken)
        assertEquals(3600L, token.expiresIn)
        assertEquals("Bearer", token.tokenType)
        assertEquals("saml:blah:2.0", token.issuedTokenType)
    }

    @Test
    fun `Should get oidc token`() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.okJson("""{"access_token":"eyJ4vaea3","expires_in":"3600","token_type":"Bearer"}"""))
        )
        val token: Token = tokenClientOld.oidcAccessToken
        assertEquals("eyJ4vaea3", token.accessToken)
        assertEquals(3600L, token.expiresIn)
        assertEquals("Bearer", token.tokenType)
        assertNull(token.issuedTokenType)

    }

    @Test
    fun `Should fail when status is not 200`() {
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.badRequest().withBody("Bad request!"))
        )
        assertThrows<RuntimeException> { tokenClientOld.oidcAccessToken }
    }

}
