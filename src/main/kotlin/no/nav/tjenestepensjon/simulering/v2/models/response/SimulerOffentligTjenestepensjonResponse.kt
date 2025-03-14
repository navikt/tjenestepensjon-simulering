package no.nav.tjenestepensjon.simulering.v2.models.response

import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode

data class SimulerOffentligTjenestepensjonResponse(
    val tpnr: String,
    val navnOrdning: String,
    val inkluderteOrdningerListe: List<String> = emptyList(),
    val leverandorUrl: String? = null,
    val utbetalingsperiodeListe: List<Utbetalingsperiode?> = emptyList(),
    var brukerErIkkeMedlemAvTPOrdning: Boolean = false,
    var brukerErMedlemAvTPOrdningSomIkkeStoettes: Boolean = false,
) {

    companion object {
        fun Companion.ikkeMedlem() = SimulerOffentligTjenestepensjonResponse("", "", emptyList(), brukerErIkkeMedlemAvTPOrdning = true)
        fun Companion.tpOrdningStoettesIkke() = SimulerOffentligTjenestepensjonResponse("", "", emptyList(), brukerErMedlemAvTPOrdningSomIkkeStoettes = true)
    }
}