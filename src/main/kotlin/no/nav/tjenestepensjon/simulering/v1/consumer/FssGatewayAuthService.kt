package no.nav.tjenestepensjon.simulering.v1.consumer

import no.nav.tjenestepensjon.simulering.service.AADClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FssGatewayAuthService(
    @param:Value("\${pen.fss.gateway.scope}") private val scope: String,
    private val adClient: AADClient
) {
    fun hentToken(): String {
        return adClient.getToken(scope)
    }
}