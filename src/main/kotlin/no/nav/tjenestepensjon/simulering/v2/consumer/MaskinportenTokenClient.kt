package no.nav.tjenestepensjon.simulering.v2.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientException

@Service
class MaskinportenTokenClient(val maskinportenToken: MaskinportenToken) {
    private val log = KotlinLogging.logger {}

    fun pensjonsimuleringToken(scope: String): String {
        return try {
            maskinportenToken.getToken(scope)
        } catch (exc: WebClientException) {
            log.error(exc) { "Error while retrieving token from provider: ${exc.message}" }
            throw ConnectToMaskinPortenException(exc.message)
        }
    }
}
