package no.nav.tjenestepensjon.simulering.model.v1.domain

import no.nav.tjenestepensjon.simulering.domain.DelytelseType

data class Delytelse(
        val pensjonstype: DelytelseType,
        val belop: Double
)