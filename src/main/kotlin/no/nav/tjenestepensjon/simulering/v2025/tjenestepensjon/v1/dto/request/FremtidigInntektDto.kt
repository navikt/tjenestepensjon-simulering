package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request

import java.time.LocalDate

data class FremtidigInntektDto(val aarligInntekt: Int, val fraOgMedDato: LocalDate)
