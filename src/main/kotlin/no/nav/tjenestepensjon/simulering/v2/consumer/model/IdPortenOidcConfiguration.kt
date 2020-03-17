package no.nav.tjenestepensjon.simulering.v2.consumer.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class IdPortenOidcConfiguration(
        @JsonProperty(value = "issuer", required = true) val issuer: String,
        @JsonProperty(value = "token_endpoint", required = true) val tokenEndpoint: String
)