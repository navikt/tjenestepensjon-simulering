package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.time.LocalDate

data class AfpOffentligLivsvarigYtelse(val ar: Int, val belop: Double, val fraOgMedDato: LocalDate, val tilOgMedDato: LocalDate)
