package no.nav.tjenestepensjon.simulering.model.v1.response

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent

data class HentStillingsprosentListeResponse(
        @get:JsonValue val stillingsprosentListe: List<Stillingsprosent> = emptyList()
)