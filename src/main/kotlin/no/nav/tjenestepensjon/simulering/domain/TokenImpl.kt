package no.nav.tjenestepensjon.simulering.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class TokenImpl(
        @JsonProperty("access_token")
        override val accessToken: String,
        @JsonProperty("expires_in")
        override val expiresIn: Long,
        @JsonProperty("token_type")
        override val tokenType: String? = null,
        @JsonProperty("issued_token_type")
        override val issuedTokenType: String? = null
) : Token {

    private val issuedAt = LocalDateTime.now()

    override val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(issuedAt.plusSeconds(expiresIn))

    override fun toString() =
            "TokenImpl{accessToken='$accessToken', expiresIn=$expiresIn, tokenType='$tokenType', issuedTokenType='$issuedTokenType', issuedAt=$issuedAt}"
}