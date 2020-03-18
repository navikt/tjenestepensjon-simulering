package no.nav.tjenestepensjon.simulering.v2.consumer.model

data class Jwks(
        val kty: String,
        val e: String,
        val use: String,
        val kid: String,
        val alg: String,
        val n: String
)