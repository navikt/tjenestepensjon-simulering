package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception

class TpOrdningStoettesIkkeException(val tpOrdning: String) : RuntimeException() {
    override val message: String
        get() = "$tpOrdning støtter ikke simulering av tjenestepensjon v2025"
}