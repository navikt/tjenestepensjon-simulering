package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Stillingsprosent @JsonCreator constructor(
        override var datoFom: LocalDate,
        var datoTom: LocalDate,
        var stillingsprosent: Double,
        var aldersgrense: Int,
        var faktiskHovedlonn: String,
        var stillingsuavhengigTilleggslonn: String
): Dateable