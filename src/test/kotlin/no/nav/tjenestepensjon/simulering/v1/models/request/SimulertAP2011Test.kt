package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.domain.DelytelseType
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
        forsteUttak = Simuleringsperiode(
                utg = ForsteUttak.UTG,
                datoFom = LocalDate.of(ForsteUttak.YEAR_FOM, ForsteUttak.MONTH_FOM, ForsteUttak.DAY_FOM),
                delingstall = ForsteUttak.DELLINGSTALL,
                poengArTom1991 = ForsteUttak.POENG_AR_TOM_1991,
                poengArFom1992 = ForsteUttak.POENG_AR_FOM_1992,
                sluttpoengtall = ForsteUttak.SLUTTPOENGTALL,
                anvendtTrygdetid = ForsteUttak.ANVENDT_TRYGDETID,
                forholdstall = ForsteUttak.FORHOLDSTALL,
                uforegradVedOmregning = ForsteUttak.UFERGRAD_VED_OMREGNING,
                stillingsprosentOffentlig = ForsteUttak.STILLINGSPROSENT_OFFENTLIG,
                delytelser = listOf(
                        Delytelse(DelytelseType.basisgp, ForsteUttak.BASISGP_),
                        Delytelse(basispt, ForsteUttak.BASISPT_),
                        Delytelse(basistp, ForsteUttak.BASISTP_),
                        Delytelse(DelytelseType.skjermingstilleg, ForsteUttak.SKJERMINGSTILLEGG_)
                )
        )

        heltUttak = Simuleringsperiode(
                utg = HeltUttak.UTG,
                datoFom = LocalDate.of(HeltUttak.YEAR_FOM, HeltUttak.MONTH_FOM, HeltUttak.DAY_FOM),
                delingstall = HeltUttak.DELLINGSTALL,
                poengArTom1991 = HeltUttak.POENG_AR_TOM_1991,
                poengArFom1992 = HeltUttak.POENG_AR_FOM_1992,
                sluttpoengtall = HeltUttak.SLUTTPOENGTALL,
                anvendtTrygdetid = HeltUttak.ANVENDT_TRYGDETID,
                forholdstall = HeltUttak.FORHOLDSTALL,
                uforegradVedOmregning = HeltUttak.UFERGRAD_VED_OMREGNING,
                stillingsprosentOffentlig = HeltUttak.STILLINGSPROSENT_OFFENTLIG,
                delytelser = listOf(
                        Delytelse(DelytelseType.basisgp, HeltUttak.BASISGP_),
                        Delytelse(basispt, HeltUttak.BASISPT_),
                        Delytelse(basistp, HeltUttak.BASISTP_),
                        Delytelse(DelytelseType.skjermingstilleg, HeltUttak.SKJERMINGSTILLEGG_)
                )
        )
    }

    @Test
    fun `Should map when fullt forstegangsuttak`() {
        forsteUttak.datoFom = LocalDate.of(2026, 1, 1)
        val simulertAP2011 = SimulertAP2011(forsteUttak, fnr)
        assertNotNull(simulertAP2011.simulertForsteuttak)
        assertNull(simulertAP2011.simulertHeltUttakEtter67Ar)
    }

    @Test
    fun `Should map when gradert forstegangsuttak`() {
        forsteUttak.datoFom = LocalDate.of(2027, 12, 3)
        heltUttak.datoFom = LocalDate.of(2030, 12, 3)
        val simulertAP2011 = SimulertAP2011(forsteUttak, heltUttak)
        assertEquals(ForsteUttak.POENG_AR_TOM_1991, simulertAP2011.simulertForsteuttak.poengArTom1991)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
        assertEquals(ForsteUttak.SLUTTPOENGTALL, simulertAP2011.simulertForsteuttak.sluttpoengtall)
        assertEquals(ForsteUttak.ANVENDT_TRYGDETID, simulertAP2011.simulertForsteuttak.anvendtTrygdetid)
        assertEquals(ForsteUttak.BASISGP_, simulertAP2011.simulertForsteuttak.basisgp)
        assertEquals(ForsteUttak.BASISPT_, simulertAP2011.simulertForsteuttak.basispt)
        assertEquals(ForsteUttak.BASISTP_, simulertAP2011.simulertForsteuttak.basistp)
        assertEquals(ForsteUttak.FORHOLDSTALL, simulertAP2011.simulertForsteuttak.forholdstall_uttak)
        assertEquals(ForsteUttak.SKJERMINGSTILLEGG_, simulertAP2011.simulertForsteuttak.skjermingstillegg)
        assertEquals(ForsteUttak.UFERGRAD_VED_OMREGNING, simulertAP2011.simulertForsteuttak.uforegradVedOmregning)

        assertEquals(HeltUttak.POENG_AR_TOM_1991, simulertAP2011.simulertHeltUttakEtter67Ar?.poengArTom1991)
        assertEquals(HeltUttak.POENG_AR_FOM_1992, simulertAP2011.simulertHeltUttakEtter67Ar?.poengArFom1992)
        assertEquals(HeltUttak.SLUTTPOENGTALL, simulertAP2011.simulertHeltUttakEtter67Ar?.sluttpoengtall)
        assertEquals(HeltUttak.ANVENDT_TRYGDETID, simulertAP2011.simulertHeltUttakEtter67Ar?.anvendtTrygdetid)
        assertEquals(HeltUttak.BASISGP_, simulertAP2011.simulertHeltUttakEtter67Ar?.basisgp)
        assertEquals(HeltUttak.BASISPT_, simulertAP2011.simulertHeltUttakEtter67Ar?.basispt)
        assertEquals(HeltUttak.BASISTP_, simulertAP2011.simulertHeltUttakEtter67Ar?.basistp)
        assertEquals(HeltUttak.FORHOLDSTALL, simulertAP2011.simulertHeltUttakEtter67Ar?.forholdstall_uttak)
        assertEquals(HeltUttak.SKJERMINGSTILLEGG_, simulertAP2011.simulertHeltUttakEtter67Ar?.skjermingstillegg)
        assertEquals(HeltUttak.UFERGRAD_VED_OMREGNING, simulertAP2011.simulertHeltUttakEtter67Ar?.uforegradVedOmregning)
    }

    @Test
    fun `Gradert and helt uttak before 67`() {
        forsteUttak.datoFom = LocalDate.of(2022, 1, 1)
        heltUttak.datoFom = LocalDate.of(2024, 6, 6)
        val simulertAP2011 = SimulertAP2011(forsteUttak, fnr)
        assertNotNull(simulertAP2011.simulertForsteuttak)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
        assertNull(simulertAP2011.simulertHeltUttakEtter67Ar)
    }

    @Test
    fun `Gradert before 67 and helt uttak after 67`() {
        forsteUttak.datoFom = LocalDate.of(2024, 4, 5)
        heltUttak.datoFom = LocalDate.of(2028, 10, 11)
        val simulertAP2011 = SimulertAP2011(forsteUttak, heltUttak)
        assertNotNull(simulertAP2011.simulertForsteuttak)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
        assertNotNull(simulertAP2011.simulertHeltUttakEtter67Ar)
        assertEquals(HeltUttak.POENG_AR_FOM_1992, simulertAP2011.simulertHeltUttakEtter67Ar?.poengArFom1992)
    }

    @Test
    fun `Gradert after 67 and helt uttak after 67`() {
        forsteUttak.datoFom = LocalDate.of(2029, 2, 1)
        heltUttak.datoFom = LocalDate.of(2030, 5, 8)
        val simulertAP2011 = SimulertAP2011(forsteUttak, heltUttak)
        assertNotNull(simulertAP2011.simulertForsteuttak)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
        assertNotNull(simulertAP2011.simulertHeltUttakEtter67Ar)
        assertEquals(HeltUttak.POENG_AR_FOM_1992, simulertAP2011.simulertHeltUttakEtter67Ar?.poengArFom1992)
    }

    @Test
    fun `Helt uttak before 67`() {
        forsteUttak.datoFom = LocalDate.of(2025, 2, 1)
        val simulertAP2011 = SimulertAP2011(forsteUttak, fnr)
        assertNotNull(simulertAP2011.simulertForsteuttak)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
        assertNull(simulertAP2011.simulertHeltUttakEtter67Ar)
    }

    @Test
    fun `Helt uttak after 67`() {
        forsteUttak.datoFom = LocalDate.of(2029, 2, 1)
        val simulertAP2011 = SimulertAP2011(forsteUttak, fnr)
        assertNotNull(simulertAP2011.simulertForsteuttak)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
        assertNotNull(simulertAP2011.simulertHeltUttakEtter67Ar)
        assertEquals(ForsteUttak.POENG_AR_FOM_1992, simulertAP2011.simulertForsteuttak.poengArFom1992)
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