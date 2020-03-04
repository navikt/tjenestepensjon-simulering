package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Pensjonsbeholdningperiode(
        override var datoFom: LocalDate,
        var pensjonsbeholdning: Int,
        var garantipensjonsbeholdning: Int = 0,
        var garantilleggsbeholdning: Int = 0
) : Dateable