package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Utbetalingsperiode(
        var grad: Int,
        var arligUtbetaling: Double,
        override var datoFom: LocalDate,
        var datoTom: LocalDate,
        var ytelsekode: String,
        var mangelfullSimuleringkode: String
) : Dateable