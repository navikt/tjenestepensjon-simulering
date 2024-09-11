package no.nav.tjenestepensjon.simulering.v2.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.stereotype.Service

@Service
class MaskinportenTokenClient(val maskinportenToken: MaskinportenToken) {
    private val log = KotlinLogging.logger {}

    fun pensjonsimuleringToken(): String {
        log.info { "Henter maskinporten token" }
        return try {
            maskinportenToken.getToken()
        } catch (exc: Throwable) {
            log.warn { "Error while retrieving token from provider: ${exc.message}" }
            throw ConnectToMaskinPortenException(exc.message)
        }
    }
}
