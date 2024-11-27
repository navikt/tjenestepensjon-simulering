package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import java.time.LocalDate

data class Maanedsutbetaling(
    val fraOgMedDato: LocalDate,
    val fraOgMedAlder: Alder,
    var maanedsBeloep: Int,
)
