package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception

class TjenestepensjonSimuleringException : RuntimeException() {
    override val message: String
        get() = "Feil ved simulering av tjenestepensjon"
}