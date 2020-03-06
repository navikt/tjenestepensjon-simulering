package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

class Inntekt(
        override var datoFom: LocalDate,
        val inntekt: Double
) : Dateable