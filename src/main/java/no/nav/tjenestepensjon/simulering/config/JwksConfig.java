package no.nav.tjenestepensjon.simulering.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwksConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JwksConfig.class);
    private String issuerUrls;

    @Value("${JWKS_URLS}")
    public void setIssuerUrls(String issuerUrls) {
        this.issuerUrls = issuerUrls;
    }

    @Bean
    public List<URL> jwksUrls() {
        List<String> urls = List.of(issuerUrls.split("\\|"));
        return urls.stream().map(JwksConfig::toUrl).collect(Collectors.toList());
    }

    private static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOG.error("Exception while converting property: {} to URL", url);
        }
        return null;
    }
}
