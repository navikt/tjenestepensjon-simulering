package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception

class TomSimuleringFraTpOrdningException(val tpOrdning: String) : RuntimeException() {
    override val message: String
        get() = "tom liste eller manglende simulering fra $tpOrdning"
}