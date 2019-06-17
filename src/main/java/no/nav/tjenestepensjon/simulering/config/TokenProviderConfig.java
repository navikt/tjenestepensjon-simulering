package no.nav.tjenestepensjon.simulering.config;

import static io.micrometer.core.instrument.util.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.Map;

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

    @Bean
    public Map<String, String> issuerJwksMap() {
        return createFromEnv(issuerJwksMap);
    }

    private Map<String, String> createFromEnv(String issuerJwksMap) {
        Map<String, String> map = new HashMap<>();
        String[] issuerJwks = issuerJwksMap.split("\\|");
        for (String s : issuerJwks) {
            String[] data = s.split(",");
            assert data.length == 2;
            assert isNotEmpty(data[0]) && isNotEmpty(data[1]);
            map.put(data[0], data[1]);
        }
        return map;
    }
}
