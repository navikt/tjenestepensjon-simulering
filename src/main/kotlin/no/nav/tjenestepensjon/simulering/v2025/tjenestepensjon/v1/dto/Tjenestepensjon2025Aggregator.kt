package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.*

object Tjenestepensjon2025Aggregator {

    fun aggregerVellykketRespons(simulertTjenestepensjon: SimulertTjenestepensjonMedMaanedsUtbetalinger, tpOrdninger: List<String>) =
        SimulerTjenestepensjonResponseDto(
            relevanteTpOrdninger = tpOrdninger,
            simuleringsResultatStatus = SimuleringsResultatStatusDto(ResultatTypeDto.SUCCESS),
            simuleringsResultat = SimuleringsResultatDto(
                tpLeverandoer = simulertTjenestepensjon.tpLeverandoer,
                utbetalingsperioder = aggregerTilAarligePerioder(simulertTjenestepensjon.utbetalingsperioder),
                betingetTjenestepensjonErInkludert = simulertTjenestepensjon.betingetTjenestepensjonErInkludert
            )
        )

    fun aggregerTilAarligePerioder(maanedsutbetalinger: List<Maanedsutbetaling>): List<UtbetalingPerAar> {
        val aarligeUtbetalinger = mutableMapOf<Int, MutableMap<Int, Int>>() //år -> månedsnummer(1-12) -> beløp for måneden

        maanedsutbetalinger.forEach { maanedsutbetaling ->
            val (startAar, startMaaned) = maanedsutbetaling.fraOgMedAlder.let { it.aar to it.maaneder + 1 }

            // Bruk beløp for start år-måned og fremtidige år
            (startAar..SISTE_UTBETALINGSAAR).forEach { aar ->
                val maanedFra = if (aar == startAar) startMaaned else 1
                aarligeUtbetalinger
                    .getOrPut(aar) { mutableMapOf() }
                    .putAll((maanedFra..MAANEDER_I_AAR).associateWith { maanedsutbetaling.maanedsBeloep })
            }
        }

        // Summer beløp per år
        return aarligeUtbetalinger
            .map { (aar, maanedToBeloep) -> UtbetalingPerAar(aar, maanedToBeloep.values.sum()) }
            .sortedBy { it.aar }
    }

    const val MAANEDER_I_AAR = 12
    const val SISTE_UTBETALINGSAAR = 85
}