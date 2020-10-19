package no.nav.tjenestepensjon.simulering.v2.consumer

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import no.nav.security.maskinporten.client.MaskinportenClient
import no.nav.security.maskinporten.client.MaskinportenConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MaskinportenTokenProvider(
        @Value("\${PENSJONSIMULERING_SCOPE}")
        val pensjonsimuleringScope: String,

        @Value("\${TPREGISTERET_SCOPE}")
        val tpregisteretScope: String,

        @Value("\${client_id}")
        val clientId: String,

        @Value("\${MASKINPORTEN_URL}")
        val maskinportenUrl: String,

        @Value("\${jwk_private}")
        privateKeys: String
) {

    private val privateKey = JWKSet.parse(privateKeys).keys.first() as RSAKey

    val pensjonsimuleringClient = MaskinportenClient(MaskinportenConfig(
            maskinportenUrl,
            clientId,
            privateKey,
            pensjonsimuleringScope,
            120
    ))

    val tpregisteretClient = MaskinportenClient(MaskinportenConfig(
            maskinportenUrl,
            clientId,
            privateKey,
            tpregisteretScope,
            120
    ))

    fun generatePensjonsimuleringToken() = pensjonsimuleringClient.maskinportenTokenString
    fun generateTpregisteretToken() = tpregisteretClient.maskinportenTokenString
}