package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import no.nav.tjenestepensjon.simulering.service.FeatureToggleService
import no.nav.tjenestepensjon.simulering.service.FeatureToggleService.Companion.PEN_715_SIMULER_SPK
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025ServiceTest.Companion.dummyRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate

@SpringBootTest
class SPKTjenestepensjonServiceTest {

    @MockitoBean
    private lateinit var client: SPKTjenestepensjonClient

    @MockitoBean
    private lateinit var featureToggleService: FeatureToggleService

    @Autowired
    private lateinit var spkTjenestepensjonService: SPKTjenestepensjonService

    @Test
    fun `simuler gruppering og sortering av tjenestepensjon fra spk`() {
        val req = dummyRequest("1963-02-05")
        `when`(client.simuler(req, "3010")).thenReturn(dummyResult())
        `when`(featureToggleService.isEnabled(PEN_715_SIMULER_SPK)).thenReturn(true)

        val res : Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> = spkTjenestepensjonService.simuler(req,"3010")

        assertTrue(res.isSuccess)
        val tjenestepensjon = res.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals(SPKMapper.PROVIDER_FULLT_NAVN, tjenestepensjon!!.tpLeverandoer)
        assertFalse(tjenestepensjon.betingetTjenestepensjonErInkludert)
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

    @Test
    fun `simuler med BTP fra spk`() {
        val req = dummyRequest("1963-02-05")
        `when`(client.simuler(req,"3010")).thenReturn(dummyResult(inkluderBTP = true))
        `when`(featureToggleService.isEnabled(PEN_715_SIMULER_SPK)).thenReturn(true)

        val res : Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> = spkTjenestepensjonService.simuler(req,"3010")

        assertTrue(res.isSuccess)
        val tjenestepensjon = res.getOrNull()
        assertNotNull(tjenestepensjon)
        assertTrue(tjenestepensjon!!.betingetTjenestepensjonErInkludert)
    }

    @Test
    fun `afp fjernes fra utbetalingsperioder fra spk`() {
        val req = dummyRequest("1963-02-05")
        `when`(featureToggleService.isEnabled(PEN_715_SIMULER_SPK)).thenReturn(true)
        `when`(client.simuler(req,"3010")).thenReturn(Result.success(SimulertTjenestepensjon(
            tpLeverandoer = "spk",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-03-01"),
                    maanedligBelop = 3000,
                    ytelseType = "OAFP"
                ),
            ),
            betingetTjenestepensjonErInkludert = false
        )))

        val res : Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> = spkTjenestepensjonService.simuler(req,"3010")

        assertTrue(res.isSuccess)
        val tjenestepensjon = res.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals(SPKMapper.PROVIDER_FULLT_NAVN, tjenestepensjon!!.tpLeverandoer)
        assertFalse(tjenestepensjon.betingetTjenestepensjonErInkludert)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertTrue(tjenestepensjon.utbetalingsperioder.isEmpty())
    }

    @Test
    fun `simulering skal ikke gjoeres if feature toggle er av`() {
        val req = dummyRequest("1963-02-05")
        `when`(client.simuler(req,"3010")).thenReturn(dummyResult(inkluderBTP = true))
        `when`(featureToggleService.isEnabled(PEN_715_SIMULER_SPK)).thenReturn(false)

        val res : Result<SimulertTjenestepensjonMedMaanedsUtbetalinger> = spkTjenestepensjonService.simuler(req,"3010")

        assertTrue(res.isFailure)
        val tjenestepensjonException = res.exceptionOrNull()
        assertNotNull(tjenestepensjonException)
        assertTrue(tjenestepensjonException is TjenestepensjonSimuleringException)
        assertTrue(tjenestepensjonException!!.message!!.contains("Simulering av tjenestepensjon hos SPK er slått av"))
    }

    fun dummyResult(inkluderBTP: Boolean = false) : Result<SimulertTjenestepensjon> {
        return Result.success(SimulertTjenestepensjon(
            tpLeverandoer = "spk",
            ordningsListe = listOf(Ordning("3010")),
            utbetalingsperioder = listOf(
                Utbetalingsperiode(
                    fom = LocalDate.parse("2026-03-01"),
                    maanedligBelop = 3000,
                    ytelseType = "SAERALDERSPAASLAG"
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
            ),
            betingetTjenestepensjonErInkludert = inkluderBTP
        ))
    }

}