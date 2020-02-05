package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Inntekt @JsonCreator constructor(
        override var datoFom: LocalDate,
        var inntekt: Double
) : Dateable