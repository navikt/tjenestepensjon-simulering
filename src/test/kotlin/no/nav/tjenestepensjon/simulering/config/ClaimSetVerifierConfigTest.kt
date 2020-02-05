package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.ClaimSetVerifierConfig.IssuerClaimVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException

internal class ClaimSetVerifierConfigTest {
    @Test
    fun shouldValidateIssuer() {
        val invalid = IssuerClaimVerifier(setOf("http://bogusissuer.com"))
        Assertions.assertThrows(InvalidTokenException::class.java) { invalid.verify(claims) }
        val valid = IssuerClaimVerifier(setOf("http://localhost.com:8080"))
        Assertions.assertDoesNotThrow { valid.verify(claims) }
    }

    companion object {
        private var claims: Map<String, Any> = mapOf("iss" to "http://localhost.com:8080")
    }
}