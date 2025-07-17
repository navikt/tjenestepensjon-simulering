package no.nav.tjenestepensjon.simulering.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class TokenImpl(
        @param:JsonProperty("access_token")
        override val accessToken: String,
        @param:JsonProperty("expires_in")
        override val expiresIn: Long,
        @param:JsonProperty("token_type")
        override val tokenType: String? = null,
        @param:JsonProperty("issued_token_type")
        override val issuedTokenType: String? = null
) : Token {

    private val issuedAt = LocalDateTime.now()

    override val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(issuedAt.plusSeconds(expiresIn))

    override fun toString() =
            "TokenImpl{accessToken='$accessToken', expiresIn=$expiresIn, tokenType='$tokenType', issuedTokenType='$issuedTokenType', issuedAt=$issuedAt}"
}