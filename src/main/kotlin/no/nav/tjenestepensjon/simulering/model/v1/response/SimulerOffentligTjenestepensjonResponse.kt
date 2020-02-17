package no.nav.tjenestepensjon.simulering.model.v1.response

data class SimulerOffentligTjenestepensjonResponse(
        var simulertPensjonListe: List<SimulertPensjon> = emptyList()
)