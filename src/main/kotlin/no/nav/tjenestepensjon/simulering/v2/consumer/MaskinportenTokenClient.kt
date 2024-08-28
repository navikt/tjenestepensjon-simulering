package no.nav.tjenestepensjon.simulering.v2.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MaskinportenTokenClient {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var maskinportenToken: MaskinportenToken

    fun pensjonsimuleringToken(): String {
        return try {
            maskinportenToken.getToken()
        } catch (exc: Throwable) {
            log.warn { "Error while retrieving token from provider: ${exc.message}" }
            throw ConnectToMaskinPortenException(exc.message)
        }
    }
}
