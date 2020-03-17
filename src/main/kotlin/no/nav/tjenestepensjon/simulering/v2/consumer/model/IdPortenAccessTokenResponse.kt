package no.nav.tjenestepensjon.simulering.v2.consumer.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class IdPortenAccessTokenResponse(
        @JsonProperty(value = "access_token", required = true) val accessToken: String,
        @JsonProperty(value = "expires_in", required = true) val expiresIn: Int,
        @JsonProperty(value = "scope", required = true) val scope: String
)