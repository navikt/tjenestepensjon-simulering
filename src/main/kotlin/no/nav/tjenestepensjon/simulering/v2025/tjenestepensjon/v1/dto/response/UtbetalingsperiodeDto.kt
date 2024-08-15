package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response

import java.time.LocalDate

data class UtbetalingsperiodeDto(val fraOgMedDato: LocalDate, val manedligUtbetaling: Int, val ytelseType: String)
