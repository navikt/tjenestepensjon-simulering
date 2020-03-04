package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Stillingsprosent(
        override var datoFom: LocalDate,
        var datoTom: LocalDate?,
        var stillingsprosent: Double,
        var aldersgrense: Int,
        var faktiskHovedlonn: String,
        var stillingsuavhengigTilleggslonn: String
): Dateable