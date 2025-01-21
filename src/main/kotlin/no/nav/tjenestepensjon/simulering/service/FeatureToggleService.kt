package no.nav.tjenestepensjon.simulering.service

interface FeatureToggleService {

    fun isEnabled(featureName: String): Boolean

    companion object {
        const val PEN_715_SIMULER_SPK = "tjenestepensjon-simulering.hent-oftp-fra-spk"
        const val SIMULER_KLP = "tjenestepensjon-simulering.hent-oftp-fra-klp"
    }
}