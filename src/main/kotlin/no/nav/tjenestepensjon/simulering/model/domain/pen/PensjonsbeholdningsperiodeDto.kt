package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.util.*

data class PensjonsbeholdningsperiodeDto (
    val datoFom: Date,
    val pensjonsbeholdning: Double,
    val garantipensjonsbeholdning: Double,
    val garantitilleggsbeholdning: Double,
)

