package no.nav.tjenestepensjon.simulering.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenImpl implements Token {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("issued_token_type")
    private String issuedTokenType;

    private final LocalDateTime issuedAt = LocalDateTime.now();

    public TokenImpl() {
    }

    public TokenImpl(String accessToken, Long expiresIn, String tokenType, String issuedTokenType) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.issuedTokenType = issuedTokenType;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public Long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String getIssuedTokenType() {
        return issuedTokenType;
    }

    @Override
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(issuedAt.plusSeconds(expiresIn));
    }

    @Override
    public String toString() {
        return "TokenImpl{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                ", issuedTokenType='" + issuedTokenType + '\'' +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
