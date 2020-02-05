package no.nav.tjenestepensjon.simulering.config

import java.util.*

object ApplicationProperties : Properties() {
    init {
        load(javaClass.getResourceAsStream("application.properties"))
    }

    val SAML_SECURITY_CONTEXT_URL: String = getProperty("SAML_SECURITY_CONTEXT_URL")

    val SIMULER_OFFENTLIG_TJENESTEPENSJON_URL: String = getProperty("SIMULER_OFFENTLIG_TJENESTEPENSJON_URL")

    val HENT_STILLINGSPROSENT_URL: String = getProperty("HENT_STILLINGSPROSENT_URL")
}