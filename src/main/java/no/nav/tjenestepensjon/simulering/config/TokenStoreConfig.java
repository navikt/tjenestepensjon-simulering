package no.nav.tjenestepensjon.simulering.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider;
import no.nav.tjenestepensjon.simulering.config.jwk.JwkDefinitionSource;
import no.nav.tjenestepensjon.simulering.config.jwk.JwkTokenStore;
import no.nav.tjenestepensjon.simulering.config.jwk.JwkVerifyingJwtAccessTokenConverter;

@Configuration
public class TokenStoreConfig {

    private final List<TokenProvider> tokenProviders;
    private final JwtClaimsSetVerifier claimsSetVerifier;

    public TokenStoreConfig(List<TokenProvider> tokenProviders, JwtClaimsSetVerifier claimsSetVerifier) {
        this.tokenProviders = tokenProviders;
        this.claimsSetVerifier = claimsSetVerifier;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwkTokenStore(tokenProviders, jwtAccessTokenConverter(), claimsSetVerifier);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        return new JwkVerifyingJwtAccessTokenConverter(new JwkDefinitionSource(tokenProviders));
    }
}
