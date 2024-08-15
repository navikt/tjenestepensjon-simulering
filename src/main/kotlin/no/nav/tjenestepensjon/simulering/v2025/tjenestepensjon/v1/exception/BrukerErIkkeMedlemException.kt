package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception

class BrukerErIkkeMedlemException : RuntimeException() {
    override val message: String
        get() = "Bruker er ikke medlem av en offentlig tjenestepensjonsordning"
}