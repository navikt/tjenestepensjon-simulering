package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto

import java.time.LocalDate

data class LoggableKLPSimulerTjenestepensjonRequest(
    val uttaksListe: List<Uttak>,
    val fremtidigInntektsListe: List<FremtidigInntekt>,
    val arIUtlandetEtter16: Int,
    val epsPensjon: Boolean,
    val eps2G: Boolean
)

data class KLPSimulerTjenestepensjonRequest(
    val personId: String,
    val uttaksListe: List<Uttak>,
    val fremtidigInntektsListe: List<FremtidigInntekt>,
    val arIUtlandetEtter16: Int,
    val epsPensjon: Boolean,
    val eps2G: Boolean
)

data class Uttak(
    val ytelseType: String,
    val fraOgMedDato: LocalDate,
    val uttaksgrad: Int
)

data class FremtidigInntekt(
    val fraOgMedDato: LocalDate,
    val arligInntekt: Int
)
