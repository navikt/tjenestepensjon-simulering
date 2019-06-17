package no.nav.tjenestepensjon.simulering.config;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.DelegatingJwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Configuration
public class ClaimSetVerifierConfig {

    private final List<URL> jwksUrls;

    public ClaimSetVerifierConfig(List<URL> jwksUrls) {
        this.jwksUrls = jwksUrls;
    }

    @Bean
    public JwtClaimsSetVerifier claimsSetVerifier() {
        return new DelegatingJwtClaimsSetVerifier(List.of(new IssuerClaimVerifier(jwksUrls)));
    }

    static class IssuerClaimVerifier implements JwtClaimsSetVerifier {
        private static final String ISS_CLAIM = "iss";
        private final List<URL> jwksUrls;

        public IssuerClaimVerifier(List<URL> jwksUrls) {
            Assert.notNull(jwksUrls, "jwksUrls cannot be null");
            Assert.notEmpty(jwksUrls, "jwksUrls cannot be empty");
            this.jwksUrls = jwksUrls;
        }

        private static String getHostName(URL url) {
            String port = url.getPort() != -1 ? ":" + url.getPort() : "";
            return url.getProtocol() + "://" + url.getHost() + port;
        }

        @Override
        public void verify(Map<String, Object> claims) {
            if (!CollectionUtils.isEmpty(claims) && claims.containsKey(ISS_CLAIM)) {
                String jwtIssuer = (String) claims.get(ISS_CLAIM);
                List<String> hostnames = jwksUrls.stream().map(IssuerClaimVerifier::getHostName).collect(Collectors.toList());
                if (!hostnames.contains(jwtIssuer)) {
                    throw new InvalidTokenException("Invalid issuers (iss) claim: " + jwtIssuer);
                }
            }
        }
    }
}
