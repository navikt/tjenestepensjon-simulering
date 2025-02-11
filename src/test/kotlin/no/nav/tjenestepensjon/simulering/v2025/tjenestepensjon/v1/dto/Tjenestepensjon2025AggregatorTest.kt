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
    fun `aggreger varierende maanedsperioder inkludert ved aarskifte til perioder med slutt alder`() {
        val simulertTjenestepensjon = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "tpLeverandoer",
            tpNummer = "5555",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2025-03-01"),
                    maanedsBeloep = 1000,
                    fraOgMedAlder = Alder(aar = 62, maaneder = 6)
                ),
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2025-09-01"),
                    maanedsBeloep = 2000,
                    fraOgMedAlder = Alder(aar = 63, maaneder = 0)
                ),
                Maanedsutbetaling(
                    fraOgMedDato = LocalDate.parse("2030-01-01"),
                    maanedsBeloep = 5000,
                    fraOgMedAlder = Alder(aar = 68, maaneder = 4)
                ),
            ),
            aarsakIngenUtbetaling = emptyList()
        )
        val ordninger = listOf("tpLeverandoer")

        val result: SimulerTjenestepensjonResponseDto = Tjenestepensjon2025Aggregator.aggregerVellykketRespons(simulertTjenestepensjon, ordninger)

        assertEquals(ResultatTypeDto.SUCCESS, result.simuleringsResultatStatus.resultatType)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder.size, result.simuleringsResultat?.utbetalingsperioder?.size)
        assertEquals(simulertTjenestepensjon.tpLeverandoer, result.simuleringsResultat?.tpLeverandoer)
        assertEquals(simulertTjenestepensjon.tpNummer, result.simuleringsResultat?.tpNummer)

        assertEquals(simulertTjenestepensjon.utbetalingsperioder[0].fraOgMedAlder, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.startAlder)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder[1].fraOgMedAlder.aar - 1, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.sluttAlder?.aar)
        assertEquals(11, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.sluttAlder?.maaneder)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder[0].maanedsBeloep, result.simuleringsResultat?.utbetalingsperioder?.get(0)?.maanedligBeloep)

        assertEquals(simulertTjenestepensjon.utbetalingsperioder[1].fraOgMedAlder, result.simuleringsResultat?.utbetalingsperioder?.get(1)?.startAlder)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder[2].fraOgMedAlder.aar, result.simuleringsResultat?.utbetalingsperioder?.get(1)?.sluttAlder?.aar)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder[2].fraOgMedAlder.maaneder - 1, result.simuleringsResultat?.utbetalingsperioder?.get(1)?.sluttAlder?.maaneder)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder[1].maanedsBeloep, result.simuleringsResultat?.utbetalingsperioder?.get(1)?.maanedligBeloep)

        assertEquals(simulertTjenestepensjon.utbetalingsperioder[2].fraOgMedAlder, result.simuleringsResultat?.utbetalingsperioder?.get(2)?.startAlder)
        assertNull(result.simuleringsResultat?.utbetalingsperioder?.get(2)?.sluttAlder)
        assertEquals(simulertTjenestepensjon.utbetalingsperioder[2].maanedsBeloep, result.simuleringsResultat?.utbetalingsperioder?.get(2)?.maanedligBeloep)
    }

    @Test
    fun `aggregering haandterer tom liste`() {
        val simulertTjenestepensjon = SimulertTjenestepensjonMedMaanedsUtbetalinger(
            tpLeverandoer = "pensjonskasse",
            tpNummer = "999999",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = emptyList(),
            aarsakIngenUtbetaling = listOf("Ingen utbetaling"),
        )
        val ordninger = listOf("pensjonskasse")

        val result = Tjenestepensjon2025Aggregator.aggregerVellykketRespons(simulertTjenestepensjon, ordninger)

        assertEquals(ResultatTypeDto.SUCCESS, result.simuleringsResultatStatus.resultatType)
        assertTrue(result.simuleringsResultat!!.utbetalingsperioder.isEmpty())
        assertEquals(simulertTjenestepensjon.tpLeverandoer, result.simuleringsResultat!!.tpLeverandoer)
        assertEquals(simulertTjenestepensjon.tpNummer, result.simuleringsResultat!!.tpNummer)
    }
}