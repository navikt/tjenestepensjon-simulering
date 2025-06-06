package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto

import java.time.LocalDate

data class KLPSimulerTjenestepensjonResponse(
    val inkludertOrdningListe: List<InkludertOrdning>,
    val utbetalingsListe: List<Utbetaling>,
    val arsakIngenUtbetaling: List<ArsakIngenUtbetaling>,
    val betingetTjenestepensjonErInkludert: Boolean,
)

data class InkludertOrdning(
    val tpnr: String
)

data class Utbetaling(
    val fraOgMedDato: LocalDate,
    val manedligUtbetaling: Int,
    val arligUtbetaling: Int,
    val ytelseType: String
)

data class ArsakIngenUtbetaling(
    val statusKode: String,
    val statusBeskrivelse: String,
    val ytelseType: String,
)
