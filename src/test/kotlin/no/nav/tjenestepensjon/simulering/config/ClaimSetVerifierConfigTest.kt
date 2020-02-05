package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.config.ClaimSetVerifierConfig.IssuerClaimVerifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException

internal class ClaimSetVerifierConfigTest {
    @Test
    fun shouldValidateIssuer() {
        val invalid = IssuerClaimVerifier(Set.of("http://bogusissuer.com"))
        Assertions.assertThrows(InvalidTokenException::class.java) { invalid.verify(claims) }
        val valid = IssuerClaimVerifier(Set.of("http://localhost.com:8080"))
        Assertions.assertDoesNotThrow { valid.verify(claims) }
    }

    companion object {
        private var claims: Map<String, Any>? = null
        @BeforeAll
        fun beforeAll() {
            claims = java.util.Map.of<String, Any>(
                    "iss", "http://localhost.com:8080"
            )
        }
    }
}