package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.util.*

data class OpptjeningsperiodeDto(
    val stillingsprosent: Int,
    val datoFom: Date,
    val datoTom: Date,
    val faktiskHovedlonn: Int,
    val stillingsuavhengigTilleggslonn: Int,
    val aldersgrense: Int,
)
