package no.nav.tjenestepensjon.simulering.domain

interface Token {
    val accessToken: String?
    val expiresIn: Long?
    val tokenType: String
    val issuedTokenType: String
    val isExpired: Boolean
}