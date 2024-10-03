package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import java.time.LocalDate

data class SPKSimulerTjenestepensjonResponse(
    val inkludertOrdningListe: List<InkludertOrdning>,
    val utbetalingListe: List<Utbetaling>,
    val aarsakIngenUtbetaling: List<AarsakIngenUtbetaling>,
)

data class InkludertOrdning(
    val tpnr: String
)

data class Utbetaling(
    val fraOgMedDato: LocalDate,
    val delytelseListe: List<Delytelse>,
)

data class Delytelse(
    val ytelseType: String,
    val maanedligBelop: Int,
)

data class AarsakIngenUtbetaling(
    val statusKode: String,
    val statusBeskrivelse: String,
    val ytelseType: String,
)