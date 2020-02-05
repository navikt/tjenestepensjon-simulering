package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.domain.DelytelseType

data class Delytelse @JsonCreator constructor(
        var pensjonstype: DelytelseType,
        var belop: Double
)