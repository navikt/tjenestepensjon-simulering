package no.nav.tjenestepensjon.simulering.soap

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SamlConfig {
    @Value("\${SECURITY_CONTEXT_URL}")
    lateinit var samlSecurityContextUrl: String
}