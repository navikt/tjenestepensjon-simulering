package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator

data class TPOrdning @JsonCreator constructor(
        var tssId: String,
        var tpId: String
)