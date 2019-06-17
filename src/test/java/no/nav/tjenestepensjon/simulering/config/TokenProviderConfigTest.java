package no.nav.tjenestepensjon.simulering.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

class TokenProviderConfigTest {
    private TokenProviderConfig tokenProviderConfig = new TokenProviderConfig();

    @Test
    void shouldCreateMapFromDelimitedString() {
        tokenProviderConfig.setIssuerJwksMap("issuer1,http://jwsks1.com|issuer2,jwks2.com");

        Map<String, String> map = tokenProviderConfig.issuerJwksMap();
        assertThat(map.get("issuer1"), is("http://jwsks1.com"));
        assertThat(map.get("issuer2"), is("jwks2.com"));
    }

    @Test
    void shouldFailIfMissingProperties() {
        tokenProviderConfig.setIssuerJwksMap("issuer1|issuer2,jwks2.com");
        assertThrows(AssertionError.class, () -> tokenProviderConfig.issuerJwksMap());
    }

    @Test
    void shouldFailIfEmptyProperty() {
        tokenProviderConfig.setIssuerJwksMap("issuer1,|issuer2,jwks2.com");
        assertThrows(AssertionError.class, () -> tokenProviderConfig.issuerJwksMap());
    }
}