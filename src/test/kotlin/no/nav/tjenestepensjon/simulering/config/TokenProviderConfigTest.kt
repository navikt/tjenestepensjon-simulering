package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TokenProviderConfigTest {
    private val tokenProviderConfig: TokenProviderConfig = TokenProviderConfig()
    @Test
    fun shouldCreateListFromDelimitedString() {
        tokenProviderConfig.setIssuerJwksMap("issuer1,http://jwsks1.com|issuer2,jwks2.com,proxyurl:80")
        val tokenProviders: List<TokenProvider> = tokenProviderConfig.createFromEnv()
        Assert.assertThat(tokenProviders[0].getIssuer(), Matchers.`is`("issuer1"))
        Assert.assertThat(tokenProviders[0].getJwksUrl(), Matchers.`is`("http://jwsks1.com"))
        Assert.assertThat(tokenProviders[0].getProxyUrl(), Matchers.`is`(Matchers.nullValue()))
        Assert.assertThat(tokenProviders[1].getIssuer(), Matchers.`is`("issuer2"))
        Assert.assertThat(tokenProviders[1].getJwksUrl(), Matchers.`is`("jwks2.com"))
        Assert.assertThat(tokenProviders[1].getProxyUrl(), Matchers.`is`("proxyurl:80"))
    }

    @Test
    fun shouldFailIfMissingProperties() {
        tokenProviderConfig.setIssuerJwksMap("issuer1|issuer2,jwks2.com")
        Assertions.assertThrows(AssertionError::class.java) { tokenProviderConfig.createFromEnv() }
    }

    @Test
    fun shouldFailIfEmptyProperty() {
        tokenProviderConfig.setIssuerJwksMap("issuer1,|issuer2,jwks2.com")
        Assertions.assertThrows(AssertionError::class.java) { tokenProviderConfig.createFromEnv() }
    }

    @Test
    fun shouldFailIfInvalidProxyConfig() {
        tokenProviderConfig.setIssuerJwksMap("issuer2,jwks2.com,proxyurl")
        Assertions.assertThrows(IllegalStateException::class.java) { tokenProviderConfig.createFromEnv() }
    }
}