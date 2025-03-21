package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception

class IkkeSisteOrdningException(val tpOrdning: String) : RuntimeException()  {
        override val message: String
            get() = "$tpOrdning er ikke siste ordning"
}