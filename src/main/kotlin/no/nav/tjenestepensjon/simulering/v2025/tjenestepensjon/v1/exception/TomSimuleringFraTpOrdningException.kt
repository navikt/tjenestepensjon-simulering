package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception

class TomSimuleringFraTpOrdningException(val tpOrdninger: List<String>) : RuntimeException() {
    override val message: String
        get() = "$tpOrdninger støtter ikke simulering av tjenestepensjon v2025"
}