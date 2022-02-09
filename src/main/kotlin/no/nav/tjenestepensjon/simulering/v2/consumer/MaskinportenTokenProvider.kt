package no.nav.tjenestepensjon.simulering.v2.consumer

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import no.nav.pensjonsamhandling.maskinporten.client.MaskinportenClient
import no.nav.pensjonsamhandling.maskinporten.client.MaskinportenConfig
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import java.net.ProxySelector
import java.text.ParseException

@Service
class MaskinportenTokenProvider(
    @Value("\${PENSJONSIMULERING_SCOPE}") val pensjonsimuleringScope: String,
    @Value("\${client_id}") clientId: String,
    @Value("\${MASKINPORTEN_URL}") maskinportenUrl: String,
    @Value("\${jwk_private}") privateKeys: String
) {

    private val privateKey = try {
        JWKSet.parse(privateKeys).keys.first() as RSAKey
    } catch (_: ParseException) {
        RSAKey.parse(privateKeys)
    }

    private final val proxySelector: ProxySelector = ProxySelector.of(InetSocketAddress("webproxy-nais.nav.no", 8088))

    private val maskinportenClient = MaskinportenClient(
        MaskinportenConfig(
            maskinportenUrl, clientId, privateKey, 120, proxySelector
        )
    )

    fun generatePensjonsimuleringToken() = try {
        maskinportenClient.getTokenString(pensjonsimuleringScope)
    } catch (e: Throwable) {
        throw ConnectToMaskinPortenException(e.message)
    }
}
