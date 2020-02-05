package no.nav.tjenestepensjon.simulering.model.v1.response

import com.fasterxml.jackson.annotation.JsonCreator

data class SimulerOffentligTjenestepensjonResponse @JsonCreator constructor(
        var simulertPensjonListe: List<SimulertPensjon> = emptyList()
)