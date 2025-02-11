package no.nav.tjenestepensjon.simulering.v2.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.stereotype.Service

@Service
class MaskinportenTokenClient(val maskinportenToken: MaskinportenToken) {
    private val log = KotlinLogging.logger {}

    fun pensjonsimuleringToken(scope: String): String {
        return try {
            maskinportenToken.getToken(scope)
        } catch (exc: ConnectToMaskinPortenException) {
            log.error(exc) { "Error while retrieving token from provider: ${exc.message}" }
            throw exc
        }
    }
}
