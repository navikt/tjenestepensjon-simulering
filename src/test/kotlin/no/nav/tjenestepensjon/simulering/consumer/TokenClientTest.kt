package no.nav.tjenestepensjon.simulering.consumer

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.domain.Token
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class TokenClientTest {
    @Test
    fun shouldGetSamlToken() {
        wireMockServer!!.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/samltoken"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\",\"issued_token_type\":\"saml:blah:2.0\"}")))
        val token: Token = tokenClient.getSamlAccessToken()
        assertThat(token.getAccessToken(), Matchers.`is`("eyJ4vaea3"))
        assertThat(token.getExpiresIn(), Matchers.`is`(3600L))
        assertThat(token.getTokenType(), Matchers.`is`("Bearer"))
        assertThat(token.getIssuedTokenType(), Matchers.`is`("saml:blah:2.0"))
    }

    @Test
    fun shouldGetOidcToken() {
        wireMockServer!!.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")))
        val token: Token = tokenClient.getOidcAccessToken()
        assertThat(token.getAccessToken(), Matchers.`is`("eyJ4vaea3"))
        assertThat(token.getExpiresIn(), Matchers.`is`(3600L))
        assertThat(token.getTokenType(), Matchers.`is`("Bearer"))
        assertThat(token.getIssuedTokenType(), Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun shouldFailWhenStatusNot200() {
        wireMockServer!!.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.badRequest().withBody("Bad request!")))
        Assertions.assertThrows(RuntimeException::class.java) { tokenClient.getOidcAccessToken() }
    }

    companion object {
        private val tokenClient: TokenClient = TokenClient()
        private var wireMockServer: WireMockServer? = null
        @BeforeAll
        fun beforeAll() {
            wireMockServer = WireMockServer()
            wireMockServer!!.start()
            tokenClient.setUsername("username")
            tokenClient.setPassword("password")
            tokenClient.setStsUrl("http://localhost:8080")
        }

        @AfterAll
        fun afterAll() {
            wireMockServer!!.stop()
        }
    }
}