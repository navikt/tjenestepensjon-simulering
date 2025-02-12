package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain

open class SimulertTjenestepensjon(
    val tpLeverandoer: String,
    var ordningsListe: List<Ordning> = emptyList(),
    var utbetalingsperioder: List<Utbetalingsperiode> = emptyList(),
    var aarsakIngenUtbetaling: List<String> = emptyList(),
    val betingetTjenestepensjonErInkludert: Boolean,
    var serviceData: List<String> = emptyList(),
)

data class Ordning(val tpNummer: String)

open class SimulertTjenestepensjonMedMaanedsUtbetalinger(
    val tpLeverandoer: String,
    val tpNummer: String,
    var ordningsListe: List<Ordning> = emptyList(),
    var utbetalingsperioder: List<Maanedsutbetaling> = emptyList(),
    var aarsakIngenUtbetaling: List<String> = emptyList(),
    val betingetTjenestepensjonErInkludert: Boolean = false,
    var serviceData: List<String> = emptyList(),
)