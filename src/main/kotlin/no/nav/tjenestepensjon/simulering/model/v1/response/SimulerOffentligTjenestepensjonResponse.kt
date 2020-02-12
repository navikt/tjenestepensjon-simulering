package no.nav.tjenestepensjon.simulering.model.v1.response

import com.fasterxml.jackson.annotation.JsonValue

data class SimulerOffentligTjenestepensjonResponse(
        @get:JsonValue var simulertPensjonListe: List<SimulertPensjon> = emptyList()
)