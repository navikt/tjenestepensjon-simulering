package no.nav.tjenestepensjon.simulering.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.tjenestepensjon.simulering.domain.Token;

class TokenClientTest {
    private static TokenClient tokenClient = new TokenClient();

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        tokenClient.setUsername("username");
        tokenClient.setPassword("password");
        tokenClient.setStsUrl("http://localhost:8080");
    }

    @Test
    void shouldGetSamlToken() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/samltoken"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\",\"issued_token_type\":\"saml:blah:2.0\"}")));

        Token token = tokenClient.getSamlAccessToken();

        assertThat(token.getAccessToken(), is("eyJ4vaea3"));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getTokenType(), is("Bearer"));
        assertThat(token.getIssuedTokenType(), is("saml:blah:2.0"));
    }

    @Test
    void shouldGetOidcToken() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));

        Token token = tokenClient.getOidcAccessToken();

        assertThat(token.getAccessToken(), is("eyJ4vaea3"));
        assertThat(token.getExpiresIn(), is(3600L));
        assertThat(token.getTokenType(), is("Bearer"));
        assertThat(token.getIssuedTokenType(), is(nullValue()));
    }

    @Test
    void shouldFailWhenStatusNot200() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/rest/v1/sts/token"))
                .willReturn(WireMock.badRequest().withBody("Bad request!")));

        assertThrows(RuntimeException.class, () -> tokenClient.getOidcAccessToken());
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }
}