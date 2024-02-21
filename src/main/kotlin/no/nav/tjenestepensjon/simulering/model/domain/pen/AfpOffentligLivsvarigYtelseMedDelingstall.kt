package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.time.LocalDate

data class AfpOffentligLivsvarigYtelseMedDelingstall (
    val pensjonsbeholdning: Int,
    val afpYtelsePerAar: Double,
    val delingstall: Double,
    val gjelderFra: LocalDate,
    val gjelderFraAlder: Alder,
)