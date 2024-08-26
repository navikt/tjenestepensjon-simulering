package no.nav.tjenestepensjon.simulering.v1.consumer

import no.nav.tjenestepensjon.simulering.service.AADClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FssGatewayAuthService(
    @Value("\${pen.fss.gateway.scope}") private val scope: String,
    private val adClient: AADClient,
    @Value("\${spring.profiles.active:}") private val activeProfiles: String //TODO fjern etter flytting til gcp
) {
    fun hentToken(): String? {
        return if (activeProfiles.contains("gcp")) adClient.getToken(scope) else null
    }
}