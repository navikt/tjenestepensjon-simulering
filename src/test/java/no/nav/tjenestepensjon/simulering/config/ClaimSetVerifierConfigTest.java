package no.nav.tjenestepensjon.simulering.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static no.nav.tjenestepensjon.simulering.config.ClaimSetVerifierConfig.IssuerClaimVerifier;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

class ClaimSetVerifierConfigTest {

    private static Map<String, Object> claims;

    @BeforeAll
    static void beforeAll() {
        claims = Map.of(
                "iss", "http://localhost.com:8080"
        );
    }

    @Test
    void shouldValidateIssuer() {
        IssuerClaimVerifier invalid = new IssuerClaimVerifier(Set.of("http://bogusissuer.com"));
        assertThrows(InvalidTokenException.class, () -> invalid.verify(claims));

        IssuerClaimVerifier valid = new IssuerClaimVerifier(Set.of("http://localhost.com:8080"));
        assertDoesNotThrow(() -> valid.verify(claims));
    }
}