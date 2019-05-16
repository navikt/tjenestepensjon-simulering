package no.nav.tjenestepensjon.simulering.consumer;

import static no.nav.tjenestepensjon.simulering.consumer.TokenClient.TokenType.OIDC;
import static no.nav.tjenestepensjon.simulering.consumer.TokenClient.TokenType.SAML;

import java.net.URI;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.tjenestepensjon.simulering.domain.Token;
import no.nav.tjenestepensjon.simulering.domain.TokenImpl;

@Component
public class TokenClient implements TokenServiceConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TokenClient.class);

    @Value("${SERVICE_USER}")
    private String username;
    @Value("${SERVICE_USER_PASSWORD}")
    private String password;
    @Value("${STS_URL}")
    private String endpoint;

    private Token oidcToken;
    private Token samlToken;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public Token getServiceUserOidcToken() {
        return getOidcAccessToken();
    }

    @Override
    public Token getServiceUserSamlToken() {
        return getSamlAccessToken();
    }

    public synchronized Token getOidcAccessToken() {
        if (oidcToken == null || oidcToken.isExpired()) {
            oidcToken = getTokenFromProvider(OIDC);
        }
        LOG.info("Returning cached and valid oidc-token for user: {}", username);
        return oidcToken;
    }

    public synchronized Token getSamlAccessToken() {
        if (samlToken == null || samlToken.isExpired()) {
            samlToken = getTokenFromProvider(SAML);
        }
        LOG.info("Returning cached and valid saml-token for user: {}", username);
        return samlToken;
    }

    private Token getTokenFromProvider(TokenType tokenType) {
        LOG.info("Getting new access-token for user: {} from: {}", username, getUrlForType(tokenType));
        RequestEntity request = RequestEntity.get(getUrlForType(tokenType))
                .header("Authorization", "Basic" + " " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .build();
        ResponseEntity<TokenImpl> response = restTemplate.exchange(request, TokenImpl.class);
        return response.getBody();
    }

    private URI getUrlForType(TokenType tokenType) {
        return OIDC.equals(tokenType) ? getOidcEndpointUrl() : getSamlEndpointUrl();
    }

    private URI getOidcEndpointUrl() {
        return URI.create(endpoint + "/rest/v1/sts/token?grant_type=client_credentials&scope=openid");
    }

    private URI getSamlEndpointUrl() {
        return URI.create(endpoint + "/rest/v1/sts/samltoken");
    }

    enum TokenType {
        OIDC,
        SAML
    }
}
