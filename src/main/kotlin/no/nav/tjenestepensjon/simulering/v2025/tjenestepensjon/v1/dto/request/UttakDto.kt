package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request

import java.time.LocalDate

data class UttakDto(val ytelseType: String, val fraOgMedDato: LocalDate, val uttaksgrad: Int)
