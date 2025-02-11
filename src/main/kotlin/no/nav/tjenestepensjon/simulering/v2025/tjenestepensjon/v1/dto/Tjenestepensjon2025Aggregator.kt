package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
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
                tpNummer = simulertTjenestepensjon.tpNummer,
                utbetalingsperioder = aggregerTilAarligePerioder(simulertTjenestepensjon.utbetalingsperioder),
                betingetTjenestepensjonErInkludert = simulertTjenestepensjon.betingetTjenestepensjonErInkludert
            ),
            serviceData = simulertTjenestepensjon.serviceData
        )

    fun aggregerTilAarligePerioder(maanedsutbetalinger: List<Maanedsutbetaling>): List<UtbetalingPerAlder> {
        val aarligeUtbetalinger = mutableListOf<UtbetalingPerAlder>()
        for (index in maanedsutbetalinger.indices) {
            val sluttAlder = maanedsutbetalinger
                .getOrNull(index + 1)
                ?.fraOgMedAlder
                ?.let {
                    if (it.maaneder == 0) {
                        Alder(it.aar - 1, 11)
                    }
                    else{
                        Alder(it.aar, it.maaneder - 1)
                    }
                }
            aarligeUtbetalinger.add(
                UtbetalingPerAlder(
                    startAlder = maanedsutbetalinger[index].fraOgMedAlder,
                    sluttAlder = sluttAlder,
                    maanedligBeloep = maanedsutbetalinger[index].maanedsBeloep,
                )
            )
        }
        return aarligeUtbetalinger
    }
}