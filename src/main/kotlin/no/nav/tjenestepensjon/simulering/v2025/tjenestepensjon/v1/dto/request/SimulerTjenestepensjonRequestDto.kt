package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request

import java.time.LocalDate

data class SimulerTjenestepensjonRequestDto(
    val pid: String,
    val foedselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val sisteInntekt: Int? = null, //kun i V1
    val aarIUtlandetEtter16: Int,
    val brukerBaOmAfp: Boolean,
    val epsPensjon: Boolean,
    val eps2G: Boolean,
    val fremtidigeInntekter: List<SimulerTjenestepensjonFremtidigInntektDto>? = null //kun i V2
)

data class SimulerTjenestepensjonFremtidigInntektDto(val fraOgMed: LocalDate, val aarligInntekt: Int)