package no.nav.tjenestepensjon.simulering.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static no.nav.tjenestepensjon.simulering.config.ClaimSetVerifierConfig.IssuerClaimVerifier;

import java.net.URL;
import java.util.List;
import java.util.Map;

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
    void shouldValidateIssuer() throws Exception {
        IssuerClaimVerifier invalid = new IssuerClaimVerifier(List.of(new URL("http://bogusissuer.com")));
        assertThrows(InvalidTokenException.class, () -> invalid.verify(claims));

        IssuerClaimVerifier valid = new IssuerClaimVerifier(List.of(new URL("http://localhost.com:8080")));
        assertDoesNotThrow(() -> valid.verify(claims));
    }
}