package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Pensjonsbeholdningperiode @JsonCreator constructor(
        override var datoFom: LocalDate,
        var pensjonsbeholdning: Int,
        var garantipensjonsbeholdning: Int = 0,
        var garantilleggsbeholdning: Int = 0
) : Dateable