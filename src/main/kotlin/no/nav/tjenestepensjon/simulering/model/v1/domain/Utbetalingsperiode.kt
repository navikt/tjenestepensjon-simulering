package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Utbetalingsperiode @JsonCreator constructor(
        var grad: Int,
        var arligUtbetaling: Double,
        override var datoFom: LocalDate,
        var datoTom: LocalDate,
        var ytelsekode: String,
        var mangelfullSimuleringkode: String
) : Dateable