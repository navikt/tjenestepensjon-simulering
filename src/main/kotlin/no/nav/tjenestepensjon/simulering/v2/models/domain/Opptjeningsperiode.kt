package no.nav.tjenestepensjon.simulering.v2.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

class Opptjeningsperiode(
        override var datoFom: LocalDate,
        val datoTom: LocalDate?,
        val stillingsprosent: Double,
        val aldersgrense: Int?,
        val faktiskHovedlonn: String?,
        val stillingsuavhengigTilleggslonn: String?
): Dateable