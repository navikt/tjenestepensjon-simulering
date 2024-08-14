package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request

data class SimulerTjenestepensjonRequestDto(
    val fnr: String,
    val uttaksListe: List<UttakDto>,
    val fremtidigInntektListe: List<FremtidigInntektDto>,
    val aarIUtlandetEtter16: Int,
    val epsPensjon: Boolean,
    val eps2G: Boolean,
)