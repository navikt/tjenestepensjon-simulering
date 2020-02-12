package no.nav.tjenestepensjon.simulering.model.v1.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Inntekt(
        override var datoFom: LocalDate,
        var inntekt: Double
) : Dateable