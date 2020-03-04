package no.nav.tjenestepensjon.simulering.v1.models.response

data class SimulerOffentligTjenestepensjonResponse(
        var simulertPensjonListe: List<SimulertPensjon> = emptyList()
)