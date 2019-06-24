package no.nav.tjenestepensjon.simulering.config;

import static io.micrometer.core.instrument.util.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenProviderConfig {

    private String issuerJwksMap;

    @Value("${ISSUER_JWKS_MAP}")
    public void setIssuerJwksMap(String issuerJwksMap) {
        this.issuerJwksMap = issuerJwksMap;
    }

    /**
     * Parse env variable to generate a list of TokenProviders.
     * "," delimits the details of induvidual providers
     * "|" delimits different providers
     * Format of env variable: "issuer-url (reqiured),issuer-jwks-url (required), proxy-url (optional)"
     *
     * @return List of TokenProvider
     */
    @Bean
    public List<TokenProvider> createFromEnv() {
        List<TokenProvider> tokenProviders = new ArrayList<>();
        String[] issuers = issuerJwksMap.split("\\|");
        for (String s : issuers) {
            String[] data = s.split(",");
            assert data.length == 2 || data.length == 3;
            if (data.length == 2) {
                assert isNotEmpty(data[0]) && isNotEmpty(data[1]);
                tokenProviders.add(new TokenProvider(data[0], data[1]));
            } else {
                assert isNotEmpty(data[0]) && isNotEmpty(data[1]) && isNotEmpty(data[2]) && validProxyUrl(data[2]);
                tokenProviders.add(new TokenProvider(data[0], data[1], data[2]));
            }
        }
        return tokenProviders;
    }

    /**
     * @param proxyUrl host:port
     * @return true if valid
     */
    private boolean validProxyUrl(String proxyUrl) {
        String[] hostAndPort = proxyUrl.split(":");
        if (hostAndPort.length == 2) {
            return true;
        }
        throw new IllegalStateException("Proxy configuration requires both host and port!");
    }

    public static class TokenProvider {
        private final String issuer;
        private final String jwksUrl;
        private final String proxyUrl;

        public TokenProvider(String issuer, String jwksUrl) {
            this(issuer, jwksUrl, null);
        }

        public TokenProvider(String issuer, String jwksUrl, String proxyUrl) {
            this.issuer = issuer;
            this.jwksUrl = jwksUrl;
            this.proxyUrl = proxyUrl;
        }

        public String getIssuer() {
            return issuer;
        }

        public String getJwksUrl() {
            return jwksUrl;
        }

        public String getProxyUrl() {
            return proxyUrl;
        }
    }
}
