package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimulerTjenestepensjonResponseDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.ResultatTypeDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class Tjenestepensjon2025AggregatorTest {

    @Test
    fun `aggreger varierende maanedsperioder til aarlige perioder`() {
        val simulertTjenestepensjon = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "tpLeverandoer",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2025-03-01"),
                    maanedsBeloep = 1000,
                    fraOgMedAlder = Alder(aar = FOERSTE_MULIGE_UTTAKS_AAR, maaneder = 0)
                ),
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2025-09-01"),
                    maanedsBeloep = 2000,
                    fraOgMedAlder = Alder(aar = FOERSTE_MULIGE_UTTAKS_AAR, maaneder = 6)
                ),
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2026-03-01"),
                    maanedsBeloep = 3000,
                    fraOgMedAlder = Alder(aar = 63, maaneder = 0)
                ),
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2030-03-01"),
                    maanedsBeloep = 4000,
                    fraOgMedAlder = Alder(aar = 67, maaneder = 0)
                ),
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2032-01-01"),
                    maanedsBeloep = 5000,
                    fraOgMedAlder = Alder(aar = 68, maaneder = 10)
                ),
            ),
            aarsakIngenUtbetaling = emptyList()
        )
        val ordninger = listOf("tpLeverandoer")

        val result: SimulerTjenestepensjonResponseDto = Tjenestepensjon2025Aggregator.aggregerVellykketRespons(simulertTjenestepensjon, ordninger)

        assertEquals(ResultatTypeDto.SUCCESS, result.simuleringsResultatStatus.resultatType)

        val antallAarligeUtbetalinger = SISTE_UTBETALINGSAAR - FOERSTE_MULIGE_UTTAKS_AAR + 1 // inkluderer første uttaksår

        assertEquals(antallAarligeUtbetalinger, result.simuleringsResultat?.utbetalingsperioder?.size)
        assertEquals(simulertTjenestepensjon.tpLeverandoer, result.simuleringsResultat?.tpLeverandoer)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.aar)
        assertEquals(1000 * 6 + 2000 * 6, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.beloep)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR + 1, result.simuleringsResultat?.utbetalingsperioder?.get(1)?.aar)
        assertEquals(3000 * MAANEDER_I_AAR, result.simuleringsResultat?.utbetalingsperioder?.get(1)?.beloep)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR + 2, result.simuleringsResultat?.utbetalingsperioder?.get(2)?.aar)
        assertEquals(3000 * MAANEDER_I_AAR, result.simuleringsResultat?.utbetalingsperioder?.get(2)?.beloep)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR + 3, result.simuleringsResultat?.utbetalingsperioder?.get(3)?.aar)
        assertEquals(3000 * MAANEDER_I_AAR, result.simuleringsResultat?.utbetalingsperioder?.get(3)?.beloep)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR + 4, result.simuleringsResultat?.utbetalingsperioder?.get(4)?.aar)
        assertEquals(3000 * MAANEDER_I_AAR, result.simuleringsResultat?.utbetalingsperioder?.get(4)?.beloep)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR + 5, result.simuleringsResultat?.utbetalingsperioder?.get(5)?.aar)
        assertEquals(4000 * MAANEDER_I_AAR, result.simuleringsResultat?.utbetalingsperioder?.get(5)?.beloep)
        assertEquals(FOERSTE_MULIGE_UTTAKS_AAR + 6, result.simuleringsResultat?.utbetalingsperioder?.get(6)?.aar)
        assertEquals(4000 * 10 + 5000 * 2, result.simuleringsResultat?.utbetalingsperioder?.get(6)?.beloep)

        var aarAlder = FOERSTE_MULIGE_UTTAKS_AAR + 7

        for (index in 7 until result.simuleringsResultat!!.utbetalingsperioder.size) {
            assertEquals(aarAlder, result.simuleringsResultat!!.utbetalingsperioder[index].aar)
            assertEquals(5000 * 12, result.simuleringsResultat!!.utbetalingsperioder[index].beloep)
            aarAlder++
        }
    }

    @Test
    fun `aggreger en maanedsutbetaling til aarlige perioder`() {
        val simulertTjenestepensjon = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "pensjonskasse",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2026-03-01"),
                    maanedsBeloep = 1000,
                    fraOgMedAlder = Alder(aar = 63, maaneder = 3)
                )))
        val ordninger = listOf("pensjonskasse")

        val result: SimulerTjenestepensjonResponseDto = Tjenestepensjon2025Aggregator.aggregerVellykketRespons(simulertTjenestepensjon, ordninger)

        assertEquals(ResultatTypeDto.SUCCESS, result.simuleringsResultatStatus.resultatType)

        val antallAarligeUtbetalinger = SISTE_UTBETALINGSAAR - 63 + 1 // inkluderer første uttaksår
        assertEquals(antallAarligeUtbetalinger, result.simuleringsResultat?.utbetalingsperioder?.size)
        assertEquals(simulertTjenestepensjon.tpLeverandoer, result.simuleringsResultat?.tpLeverandoer)
        assertEquals(63, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.aar)
        assertEquals(1000 * (MAANEDER_I_AAR - 3), result.simuleringsResultat?.utbetalingsperioder?.get(0)?.beloep)
        for (index in 1 until result.simuleringsResultat!!.utbetalingsperioder.size) {
            assertEquals(63 + index, result.simuleringsResultat!!.utbetalingsperioder[index].aar)
            assertEquals(1000 * MAANEDER_I_AAR, result.simuleringsResultat!!.utbetalingsperioder[index].beloep)
        }
    }

    @Test
    fun `aggregering haandterer tom liste`() {
        val simulertTjenestepensjon = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "pensjonskasse",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = emptyList(),
            aarsakIngenUtbetaling = listOf("Ingen utbetaling"),
        )
        val ordninger = listOf("pensjonskasse")

        val result = Tjenestepensjon2025Aggregator.aggregerVellykketRespons(simulertTjenestepensjon, ordninger)

        assertEquals(ResultatTypeDto.SUCCESS, result.simuleringsResultatStatus.resultatType)
        assertTrue(result.simuleringsResultat!!.utbetalingsperioder.isEmpty())
        assertEquals(simulertTjenestepensjon.tpLeverandoer, result.simuleringsResultat!!.tpLeverandoer)
    }

    companion object {
        const val FOERSTE_MULIGE_UTTAKS_AAR = 62
        const val MAANEDER_I_AAR = 12
        const val SISTE_UTBETALINGSAAR = 85
    }
}