package no.nav.tjenestepensjon.simulering.v2.consumer

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import no.nav.security.maskinporten.client.MaskinportenClient
import no.nav.security.maskinporten.client.MaskinportenConfig
import no.nav.security.maskinporten.client.ProxyConfig
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.ParseException

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

    private val privateKey = try {
        JWKSet.parse(privateKeys).keys.first() as RSAKey
    } catch (_: ParseException) {
        RSAKey.parse(privateKeys)
    }

    val pensjonsimuleringClient = MaskinportenClient(MaskinportenConfig(
            maskinportenUrl,
            clientId,
            privateKey,
            pensjonsimuleringScope,
            120,
            ProxyConfig("http://webproxy.nais", 8088)
    ))

    val tpregisteretClient = MaskinportenClient(MaskinportenConfig(
            maskinportenUrl,
            clientId,
            privateKey,
            tpregisteretScope,
            120,
            ProxyConfig("http://webproxy.nais", 8088)
    ))

    fun generatePensjonsimuleringToken() = try {
        pensjonsimuleringClient.maskinportenTokenString
    } catch (e: Throwable) {
        throw ConnectToMaskinPortenException(e.message)
    }

    fun generateTpregisteretToken() = try {
        tpregisteretClient.maskinportenTokenString
    } catch (e: Throwable) {
        throw ConnectToMaskinPortenException(e.message)
    }
}