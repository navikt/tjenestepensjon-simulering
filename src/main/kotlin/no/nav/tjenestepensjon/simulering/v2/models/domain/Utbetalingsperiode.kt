package no.nav.tjenestepensjon.simulering.v2.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Utbetalingsperiode(
        var uttaksgrad: Int,
        var arligUtbetaling: Double,
        override var datoFom: LocalDate,
        var datoTom: LocalDate?,
        var ytelsekode: String
) : Dateable