package no.nav.tjenestepensjon.simulering.domain;

public interface Token {
    String getAccessToken();

    Long getExpiresIn();

    String getTokenType();

    String getIssuedTokenType();

    boolean isExpired();
}
