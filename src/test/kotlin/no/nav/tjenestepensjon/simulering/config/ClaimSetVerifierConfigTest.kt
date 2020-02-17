package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.ClaimSetVerifierConfig.IssuerClaimVerifier
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException

internal class ClaimSetVerifierConfigTest {

    private val validIssuer = "http://localhost.com:8080"
    private val claims: Map<String, Any> = mapOf("iss" to validIssuer)

    @Test
    fun `Should validate issuer`() {
        val invalid = IssuerClaimVerifier(setOf("http://bogusissuer.com"))
        assertThrows<InvalidTokenException> { invalid.verify(claims) }
        val valid = IssuerClaimVerifier(setOf(validIssuer))
        assertDoesNotThrow { valid.verify(claims) }
    }
}