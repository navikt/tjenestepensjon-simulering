package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

internal class TokenProviderConfigTest {

    private val tokenProviderConfig: TokenProviderConfig = TokenProviderConfig(null, null)

    @Test
    fun `Should create list from delimited string`() {
        tokenProviderConfig.issuerJwksMap = "issuer1,http://jwsks1.com|issuer2,jwks2.com,proxyurl:80"
        val tokenProviders: List<TokenProvider> = tokenProviderConfig.createFromEnv()
        assertEquals("issuer1", tokenProviders[0].issuer)
        assertEquals("http://jwsks1.com", tokenProviders[0].jwksUrl)
        assertNull(tokenProviders[0].proxyUrl)
        assertEquals("issuer2", tokenProviders[1].issuer)
        assertEquals("jwks2.com", tokenProviders[1].jwksUrl)
        assertEquals("proxyurl:80", tokenProviders[1].proxyUrl)
    }

    @Test
    fun `Should fail if missing properties`() {
        tokenProviderConfig.issuerJwksMap = "issuer1|issuer2,jwks2.com"
        assertThrows<AssertionError> { tokenProviderConfig.createFromEnv() }
    }

    @Test
    fun `Should fail if empty property`() {
        tokenProviderConfig.issuerJwksMap = "issuer1,|issuer2,jwks2.com"
        assertThrows<AssertionError> { tokenProviderConfig.createFromEnv() }
    }

    @Test
    fun `Should fail if invalid proxy config`() {
        tokenProviderConfig.issuerJwksMap = "issuer2,jwks2.com,proxyurl"
        assertThrows<IllegalStateException> { tokenProviderConfig.createFromEnv() }
    }
}