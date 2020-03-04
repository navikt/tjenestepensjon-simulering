package no.nav.tjenestepensjon.simulering.v1.models.response

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent

data class HentStillingsprosentListeResponse(
        @get:JsonValue val stillingsprosentListe: List<Stillingsprosent> = emptyList()
)