package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class SPKMapperTest {

    @Test
    fun mapToResponse() {
        val resp = SPKSimulerTjenestepensjonResponse(
            listOf(InkludertOrdning("3010")),
            listOf(Utbetaling(LocalDate.of(2025, 2, 1), listOf(Delytelse("BTP", 141), Delytelse("PAASLAG", 268))),
                Utbetaling(LocalDate.of(2030, 2, 1), listOf(Delytelse("OT6370", 779), Delytelse("PAASLAG", 268)))),
            listOf(AarsakIngenUtbetaling("IKKE_STOETTET", "Ikke stoettet", "SAERALDERSPAASLAG"))
        )

        val result = SPKMapper.mapToResponse(resp)

        assertEquals(1, result.ordningsListe.size)
        assertEquals("3010", result.ordningsListe[0].tpNummer)
        assertEquals(4, result.utbetalingsperioder.size)

        assertEquals(LocalDate.of(2025, 2, 1), result.utbetalingsperioder[0].fom)
        assertEquals(141, result.utbetalingsperioder[0].maanedligBelop)
        assertEquals("BTP", result.utbetalingsperioder[0].ytelseType)
        assertEquals(268, result.utbetalingsperioder[1].maanedligBelop)
        assertEquals("PAASLAG", result.utbetalingsperioder[1].ytelseType)

        assertEquals(LocalDate.of(2030, 2, 1), result.utbetalingsperioder[2].fom)
        assertEquals(779, result.utbetalingsperioder[2].maanedligBelop)
        assertEquals("OT6370", result.utbetalingsperioder[2].ytelseType)
        assertEquals(268, result.utbetalingsperioder[3].maanedligBelop)
        assertEquals("PAASLAG", result.utbetalingsperioder[3].ytelseType)

        assertEquals(1, result.aarsakIngenUtbetaling.size)
        assertEquals("Ikke stoettet: SAERALDERSPAASLAG", result.aarsakIngenUtbetaling[0])

    }
}