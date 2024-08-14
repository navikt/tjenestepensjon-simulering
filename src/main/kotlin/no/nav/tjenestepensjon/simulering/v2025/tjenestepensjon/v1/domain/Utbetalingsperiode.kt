package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain

import java.time.LocalDate

data class Utbetalingsperiode(val fom: LocalDate, val maanedligBelop: Int, val ytelseType: String)
