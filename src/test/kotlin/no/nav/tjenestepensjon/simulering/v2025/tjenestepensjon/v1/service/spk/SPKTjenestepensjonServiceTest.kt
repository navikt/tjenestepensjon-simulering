package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025ServiceTest.Companion.dummyRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate

@SpringBootTest
class SPKTjenestepensjonServiceTest {

    @MockBean
    private lateinit var client: SPKTjenestepensjonClient

    @Autowired
    private lateinit var spkTjenestepensjonService: SPKTjenestepensjonService

    @Test
    fun `simuler gruppering og sortering av tjenestepensjon fra spk`() {
        val req = dummyRequest("1963-02-05")
        `when`(client.simuler(req)).thenReturn(dummyResult())

        val res : Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> = spkTjenestepensjonService.simuler(req)

        assertTrue(res.isSuccess)
        val tjenestepensjon = res.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals("spk", tjenestepensjon!!.tpLeverandoer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals(2, tjenestepensjon.utbetalingsperioder.size)

        assertEquals(LocalDate.parse("2026-01-01"), tjenestepensjon.utbetalingsperioder[0].fraOgMedDato)
        assertEquals(3000, tjenestepensjon.utbetalingsperioder[0].maanedsBeloep)
        assertEquals(62 , tjenestepensjon.utbetalingsperioder[0].fraOgMedAlder.aar)
        assertEquals(10, tjenestepensjon.utbetalingsperioder[0].fraOgMedAlder.maaneder)

        assertEquals(LocalDate.parse("2026-03-01"), tjenestepensjon.utbetalingsperioder[1].fraOgMedDato)
        assertEquals(7000, tjenestepensjon.utbetalingsperioder[1].maanedsBeloep)
        assertEquals(63 , tjenestepensjon.utbetalingsperioder[1].fraOgMedAlder.aar)
        assertEquals(0, tjenestepensjon.utbetalingsperioder[1].fraOgMedAlder.maaneder)
    }

    fun dummyResult() : Result<SimulertTjenestepensjon> {
        return Result.success(SimulertTjenestepensjon(
            tpLeverandoer = "spk",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-03-01"),
                    maanedligBelop = 3000,
                    ytelseType = "OAFP"
                ),
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-03-01"),
                    maanedligBelop = 4000,
                    ytelseType = "OT6370"
                ),
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-01-01"),
                    maanedligBelop = 1000,
                    ytelseType = "PAASLAG"
                ),
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-01-01"),
                    maanedligBelop = 2000,
                    ytelseType = "APOF2020"
                ),
            )
        ))
    }

}