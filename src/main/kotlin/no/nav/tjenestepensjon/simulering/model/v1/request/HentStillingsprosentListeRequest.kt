package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR

data class HentStillingsprosentListeRequest @JsonCreator constructor(
        var tssEksternId: String,
        var fnr: FNR,
        var tpnr: String,
        var simuleringsKode: String
)