package no.nav.tjenestepensjon.simulering.model.v1.response

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent

data class HentStillingsprosentListeResponse @JsonCreator constructor(
        val stillingsprosentListe: List<Stillingsprosent> = emptyList()
)