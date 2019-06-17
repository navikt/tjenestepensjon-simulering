package no.nav.tjenestepensjon.simulering.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.DelegatingJwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Configuration
public class ClaimSetVerifierConfig {

    private final Map<String, String> issuerJwksMap;

    public ClaimSetVerifierConfig(Map<String, String> issuerJwksMap) {
        this.issuerJwksMap = issuerJwksMap;
    }

    @Bean
    public JwtClaimsSetVerifier claimsSetVerifier() {
        return new DelegatingJwtClaimsSetVerifier(List.of(new IssuerClaimVerifier(issuerJwksMap.keySet())));
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
