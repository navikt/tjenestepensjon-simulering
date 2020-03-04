package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Inntekt(
        override var datoFom: LocalDate,
        var inntekt: Double
) : Dateable