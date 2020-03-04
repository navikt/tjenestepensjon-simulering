package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.domain.DelytelseType

data class Delytelse(
        val pensjonstype: DelytelseType,
        val belop: Double
)