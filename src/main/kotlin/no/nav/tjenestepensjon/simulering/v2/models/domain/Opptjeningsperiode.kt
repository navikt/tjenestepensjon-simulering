package no.nav.tjenestepensjon.simulering.v2.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Opptjeningsperiode(
        override var datoFom: LocalDate,
        val datoTom: LocalDate?,
        val stillingsprosent: Double,
        val aldersgrense: Int?,
        val faktiskHovedlonn: Int?,
        val stillingsuavhengigTilleggslonn: Int?
): Dateable