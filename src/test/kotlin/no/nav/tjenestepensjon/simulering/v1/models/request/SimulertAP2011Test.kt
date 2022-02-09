package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.domain.DelytelseType.*
import no.nav.tjenestepensjon.simulering.v1.models.domain.Delytelse
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Simuleringsperiode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class SimulertAP2011Test {

    private lateinit var forsteUttak: Simuleringsperiode
    private lateinit var heltUttak: Simuleringsperiode

    @BeforeEach
    fun reset() {
        forsteUttak = ForsteUttak.run {
            Simuleringsperiode(
                utg = UTG,
                datoFom = LocalDate.of(YEAR_FOM, MONTH_FOM, DAY_FOM),
                delingstall = DELLINGSTALL,
                poengArTom1991 = POENG_AR_TOM_1991,
                poengArFom1992 = POENG_AR_FOM_1992,
                sluttpoengtall = SLUTTPOENGTALL,
                anvendtTrygdetid = ANVENDT_TRYGDETID,
                forholdstall = FORHOLDSTALL,
                uforegradVedOmregning = UFERGRAD_VED_OMREGNING,
                stillingsprosentOffentlig = STILLINGSPROSENT_OFFENTLIG,
                delytelser = listOf(
                    Delytelse(BASISGP, BASISGP_),
                    Delytelse(BASISPT, BASISPT_),
                    Delytelse(BASISTP, BASISTP_),
                    Delytelse(SKJERMINGSTILLEGG, SKJERMINGSTILLEGG_)
                )
            )
        }

        heltUttak = HeltUttak.run {
            Simuleringsperiode(
                utg = UTG,
                datoFom = LocalDate.of(YEAR_FOM, MONTH_FOM, DAY_FOM),
                delingstall = DELLINGSTALL,
                poengArTom1991 = POENG_AR_TOM_1991,
                poengArFom1992 = POENG_AR_FOM_1992,
                sluttpoengtall = SLUTTPOENGTALL,
                anvendtTrygdetid = ANVENDT_TRYGDETID,
                forholdstall = FORHOLDSTALL,
                uforegradVedOmregning = UFERGRAD_VED_OMREGNING,
                stillingsprosentOffentlig = STILLINGSPROSENT_OFFENTLIG,
                delytelser = listOf(
                    Delytelse(BASISGP, BASISGP_),
                    Delytelse(BASISPT, BASISPT_),
                    Delytelse(BASISTP, BASISTP_),
                    Delytelse(SKJERMINGSTILLEGG, SKJERMINGSTILLEGG_)
                )
            )
        }
    }

    @Test
    fun `Should map when fullt forstegangsuttak`() {
        forsteUttak.datoFom = LocalDate.of(2026, 1, 1)
        SimulertAP2011(forsteUttak, fnr).apply {
            assertNotNull(simulertForsteuttak)
            assertNull(simulertHeltUttakEtter67Ar)
        }
    }

    @Test
    fun `Should map when gradert forstegangsuttak`() {
        forsteUttak.datoFom = LocalDate.of(2027, 12, 3)
        heltUttak.datoFom = LocalDate.of(2030, 12, 3)
        SimulertAP2011(forsteUttak, heltUttak).apply {
            ForsteUttak.apply {
                simulertForsteuttak.apply {
                    assertEquals(POENG_AR_TOM_1991, poengArTom1991)
                    assertEquals(POENG_AR_FOM_1992, poengArFom1992)
                    assertEquals(SLUTTPOENGTALL, sluttpoengtall)
                    assertEquals(ANVENDT_TRYGDETID, anvendtTrygdetid)
                    assertEquals(BASISGP_, basisgp)
                    assertEquals(BASISPT_, basispt)
                    assertEquals(BASISTP_, basistp)
                    assertEquals(FORHOLDSTALL, forholdstall_uttak)
                    assertEquals(SKJERMINGSTILLEGG_, skjermingstillegg)
                    assertEquals(UFERGRAD_VED_OMREGNING, uforegradVedOmregning)
                }
            }

            HeltUttak.apply {
                simulertHeltUttakEtter67Ar!!.apply {
                    assertEquals(POENG_AR_TOM_1991, poengArTom1991)
                    assertEquals(POENG_AR_FOM_1992, poengArFom1992)
                    assertEquals(SLUTTPOENGTALL, sluttpoengtall)
                    assertEquals(ANVENDT_TRYGDETID, anvendtTrygdetid)
                    assertEquals(BASISGP_, basisgp)
                    assertEquals(BASISPT_, basispt)
                    assertEquals(BASISTP_, basistp)
                    assertEquals(FORHOLDSTALL, forholdstall_uttak)
                    assertEquals(SKJERMINGSTILLEGG_, skjermingstillegg)
                    assertEquals(UFERGRAD_VED_OMREGNING, uforegradVedOmregning)
                }
            }
        }
    }

    @Test
    fun `Gradert and helt uttak before 67`() {
        forsteUttak.datoFom = LocalDate.of(2022, 1, 1)
        heltUttak.datoFom = LocalDate.of(2024, 6, 6)
        SimulertAP2011(forsteUttak, fnr).apply {
            assertNotNull(simulertForsteuttak)
            assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertForsteuttak.poengArFom1992)
            assertNull(simulertHeltUttakEtter67Ar)
        }
    }

    @Test
    fun `Gradert before 67 and helt uttak after 67`() {
        forsteUttak.datoFom = LocalDate.of(2024, 4, 5)
        heltUttak.datoFom = LocalDate.of(2028, 10, 11)
        SimulertAP2011(forsteUttak, heltUttak).apply {
            assertNotNull(simulertForsteuttak)
            assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertForsteuttak.poengArFom1992)
            assertNotNull(simulertHeltUttakEtter67Ar)
            assertEquals(HeltUttak.POENG_AR_FOM_1992, simulertHeltUttakEtter67Ar?.poengArFom1992)
        }
    }

    @Test
    fun `Gradert after 67 and helt uttak after 67`() {
        forsteUttak.datoFom = LocalDate.of(2029, 2, 1)
        heltUttak.datoFom = LocalDate.of(2030, 5, 8)
        SimulertAP2011(forsteUttak, heltUttak).apply {
            assertNotNull(simulertForsteuttak)
            assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertForsteuttak.poengArFom1992)
            assertNotNull(simulertHeltUttakEtter67Ar)
            assertEquals(HeltUttak.POENG_AR_FOM_1992, simulertHeltUttakEtter67Ar?.poengArFom1992)
        }
    }

    @Test
    fun `Helt uttak before 67`() {
        forsteUttak.datoFom = LocalDate.of(2025, 2, 1)
        SimulertAP2011(forsteUttak, fnr).apply {
            assertNotNull(simulertForsteuttak)
            assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertForsteuttak.poengArFom1992)
            assertNull(simulertHeltUttakEtter67Ar)
        }
    }

    @Test
    fun `Helt uttak after 67`() {
        forsteUttak.datoFom = LocalDate.of(2029, 2, 1)
        SimulertAP2011(forsteUttak, fnr).apply {
            assertNotNull(simulertForsteuttak)
            assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertForsteuttak.poengArFom1992)
            assertNotNull(simulertHeltUttakEtter67Ar)
            assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertForsteuttak.poengArFom1992)
        }
    }

    private object HeltUttak {
        const val UTG = 0
        const val YEAR_FOM = 2030
        const val MONTH_FOM = 1
        const val DAY_FOM = 1
        const val DELLINGSTALL = 0.0
        const val FORHOLDSTALL = 2.5
        const val POENG_AR_TOM_1991 = 5
        const val POENG_AR_FOM_1992 = 9
        const val SLUTTPOENGTALL = 2.5
        const val ANVENDT_TRYGDETID = 31
        const val UFERGRAD_VED_OMREGNING = 35
        const val STILLINGSPROSENT_OFFENTLIG = 0
        const val BASISGP_ = 5000.0
        const val BASISPT_ = 500.0
        const val BASISTP_ = 5000.0
        const val SKJERMINGSTILLEGG_ = 700.0
    }

    private object ForsteUttak {
        const val UTG = 0
        const val YEAR_FOM = 2030
        const val MONTH_FOM = 1
        const val DAY_FOM = 1
        const val DELLINGSTALL = 0.0
        const val FORHOLDSTALL = 100.0
        const val POENG_AR_TOM_1991 = 15
        const val POENG_AR_FOM_1992 = 17
        const val SLUTTPOENGTALL = 5.5
        const val ANVENDT_TRYGDETID = 64
        const val UFERGRAD_VED_OMREGNING = 10
        const val STILLINGSPROSENT_OFFENTLIG = 0
        const val BASISGP_ = 25000.0
        const val BASISPT_ = 5000.0
        const val BASISTP_ = 15000.0
        const val SKJERMINGSTILLEGG_ = 7000.0
    }

    companion object {
        private val fnr = FNR("01016092500") //Turns 67 in 2027
    }
}
