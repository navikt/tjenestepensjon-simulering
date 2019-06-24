package no.nav.tjenestepensjon.simulering.config;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider;

class TokenProviderConfigTest {
    private TokenProviderConfig tokenProviderConfig = new TokenProviderConfig();

    @Test
    void shouldCreateListFromDelimitedString() {
        tokenProviderConfig.setIssuerJwksMap("issuer1,http://jwsks1.com|issuer2,jwks2.com,proxyurl:80");

        List<TokenProvider> tokenProviders = tokenProviderConfig.createFromEnv();
        assertThat(tokenProviders.get(0).getIssuer(), is("issuer1"));
        assertThat(tokenProviders.get(0).getJwksUrl(), is("http://jwsks1.com"));
        assertThat(tokenProviders.get(0).getProxyUrl(), is(nullValue()));

        assertThat(tokenProviders.get(1).getIssuer(), is("issuer2"));
        assertThat(tokenProviders.get(1).getJwksUrl(), is("jwks2.com"));
        assertThat(tokenProviders.get(1).getProxyUrl(), is("proxyurl:80"));
    }

    @Test
    void shouldFailIfMissingProperties() {
        tokenProviderConfig.setIssuerJwksMap("issuer1|issuer2,jwks2.com");
        assertThrows(AssertionError.class, () -> tokenProviderConfig.createFromEnv());
    }

    @Test
    void shouldFailIfEmptyProperty() {
        tokenProviderConfig.setIssuerJwksMap("issuer1,|issuer2,jwks2.com");
        assertThrows(AssertionError.class, () -> tokenProviderConfig.createFromEnv());
    }

    @Test
    void shouldFailIfInvalidProxyConfig() {
        tokenProviderConfig.setIssuerJwksMap("issuer2,jwks2.com,proxyurl");
        assertThrows(IllegalStateException.class, () -> tokenProviderConfig.createFromEnv());
    }
}