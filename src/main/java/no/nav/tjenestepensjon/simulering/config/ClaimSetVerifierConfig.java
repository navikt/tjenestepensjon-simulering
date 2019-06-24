package no.nav.tjenestepensjon.simulering.config;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.DelegatingJwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider;

@Configuration
public class ClaimSetVerifierConfig {

    private final List<TokenProvider> tokenProviders;

    public ClaimSetVerifierConfig(List<TokenProvider> tokenProviders) {
        this.tokenProviders = tokenProviders;
    }

    @Bean
    public JwtClaimsSetVerifier claimsSetVerifier() {
        return new DelegatingJwtClaimsSetVerifier(List.of(new IssuerClaimVerifier(tokenProviders.stream().map(TokenProvider::getIssuer).collect(Collectors.toSet()))));
    }

    static class IssuerClaimVerifier implements JwtClaimsSetVerifier {
        private static final String ISS_CLAIM = "iss";
        private final Set<String> issuerUrls;

        public IssuerClaimVerifier(Set<String> issuerUrls) {
            Assert.notNull(issuerUrls, "issuerUrls cannot be null");
            Assert.notEmpty(issuerUrls, "issuerUrls cannot be empty");
            this.issuerUrls = issuerUrls;
        }

        @Override
        public void verify(Map<String, Object> claims) {
            if (!CollectionUtils.isEmpty(claims) && claims.containsKey(ISS_CLAIM)) {
                String jwtIssuer = (String) claims.get(ISS_CLAIM);
                if (!issuerUrls.contains(jwtIssuer)) {
                    throw new InvalidTokenException("Invalid issuer (iss) claim: " + jwtIssuer);
                }
            }
        }
    }
}
