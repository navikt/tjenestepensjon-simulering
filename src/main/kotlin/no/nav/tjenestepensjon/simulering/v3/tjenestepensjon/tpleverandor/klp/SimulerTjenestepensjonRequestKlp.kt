package no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.tpleverandor.klp

import java.time.LocalDate

data class SimulerTjenestepensjonRequestKlp (
    val personId: String,
    val uttaksListe: List<TjenestePensjonUttak>,
    val fremtidigInntektListe: List<FremtidigInntekt>,
    val arIUtlandetEtterFylte16Ar: Int,
    val epsPensjon: Boolean,
    val eps2G: Boolean
)

data class FremtidigInntekt (
    val arligInntekt: Int,
    val fraOgMedDato: LocalDate
)

data class TjenestePensjonUttak (
    val ytelseType: YtelseType,
    val fraOgMedDato: LocalDate,
    val uttaksgrad: Int
)

enum class YtelseType {
    ALLE,
    PAASLAG,
    APOF2020,
    OAFP
}