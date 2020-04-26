package no.nav.tjenestepensjon.simulering.v2.models

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.domain.*
import no.nav.tjenestepensjon.simulering.v2.models.request.*
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import java.time.LocalDate


const val defaultFNRString = "01010101010"

const val defaultTpnr = "bogus"

val defaultFNR = FNR(defaultFNRString)

val defaultDatoFom: LocalDate = LocalDate.of(1901, 1, 1)
val defaultDatoTom: LocalDate = LocalDate.of(1901, 1, 31)


val defaultUtbetalingsperiode = Utbetalingsperiode(
        uttaksgrad = 0,
        arligUtbetaling = 0.0,
        datoFom = defaultDatoFom,
        datoTom = defaultDatoTom,
        ytelsekode = "bogus"
)

val defaultUtbetalingsperiodeListe = listOf(
        defaultUtbetalingsperiode,
        null
)

val defaultSimulerOffentligTjenestepensjonResponse = SimulerOffentligTjenestepensjonResponse(
        tpnr = "bogus",
        navnOrdning = "bogus",
        inkluderteOrdningerListe = listOf("bogus"),
        leverandorUrl = "bogus",
        utbetalingsperiodeListe = defaultUtbetalingsperiodeListe
)

val defaultOpptjeningsperiode = Opptjeningsperiode(
        aldersgrense = 0,
        datoFom = defaultDatoFom,
        datoTom = defaultDatoTom,
        stillingsprosent = 0.0,
        stillingsuavhengigTilleggslonn = 0,
        faktiskHovedlonn = 0
)

val defaultSimuleringsperiode = Simuleringsperiode(
        datoFom = defaultDatoFom,
        stillingsprosentOffentlig = 0,
        folketrygdUttaksgrad = 0,
        simulerAFPOffentligEtterfulgtAvAlder = true
)

val defaultSimulertAFPPrivat = SimulertAFPPrivat(
        afpOpptjeningTotalbelop = 0,
        kompensasjonstillegg = 0.0
)


val defaultSimulertAFPOffentlig = SimulertAFPOffentlig(
        simulertAFPOffentligBrutto = 0,
        tpi = 0
)


val defaultPensjonsbeholdningperiode = Pensjonsbeholdningsperiode(
        datoFom = defaultDatoFom,
        pensjonsbeholdning = 0,
        garantipensjonsbeholdning = 0,
        garantitilleggsbeholdning = 0
)

val defaultInntekt = Inntekt(
        datoFom = defaultDatoFom,
        inntekt = 0.0
)

val defaultSimuleringsdata = Simuleringsdata(
        datoFom = defaultDatoFom,
        andvendtTrygdetid = 0,
        poengArTom1991 = 0,
        poengArFom1992 = 0,
        uforegradVedOmregning = 0,
        sluttpoengtall = 0.0,
        basisgp = 0.0,
        basistp = 0.0,
        basispt = 0.0,
        delingstallUttak = 0.0,
        forholdstallUttak = 0.0
)

val defaultTpForhold = TpForhold(
        tpnr = "bogus",
        opptjeningsperiodeListe = listOf(defaultOpptjeningsperiode)
)

val defaultSimulertPensjonRequest = SimulerPensjonRequest(
        fnr = defaultFNR,
        fodselsdato = "19010101",
        sisteTpnr = "bogus",
        sivilstandkode = SivilstandCodeEnum.GIFT,
        sprak = "bogus",
        pensjonsbeholdningsperiodeListe = listOf(defaultPensjonsbeholdningperiode),
        simuleringsdataListe = listOf(defaultSimuleringsdata),
        simuleringsperiodeListe = listOf(defaultSimuleringsperiode),
        inntektListe = listOf(defaultInntekt),
        simulertAFPPrivat = defaultSimulertAFPPrivat,
        simulertAFPOffentlig = defaultSimulertAFPOffentlig,
        tpForholdListe = listOf(defaultTpForhold)
)

val defaultOpptjeningsperiodeListe: List<Opptjeningsperiode> = listOf(defaultOpptjeningsperiode)

val defaultTPOrdning = TPOrdning("bogus", "bogus")

const val defaultFomDateString = "1901-01-01"
const val defaultTomDateString = "1901-01-31"

const val defaultSimulerOffentligTjenestepensjonResponseJson = """{"tpnr":"bogus","navnOrdning":"bogus","inkluderteOrdningerListe":["bogus"],"leverandorUrl":"bogus","utbetalingsperiodeListe":[{"uttaksgrad":0,"arligUtbetaling":0.0,"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","ytelsekode":"bogus"},null]}"""


const val defaultOppjeningsperiodeJson = """{"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":0,"stillingsuavhengigTilleggslonn":0}"""
const val defaultOppjeningsperiodeListeJson = """[$defaultOppjeningsperiodeJson]"""
const val defaultSimulertAFPPrivatJson = """{"afpOpptjeningTotalbelop":0,"kompensasjonstillegg":0.0}"""
const val defaultSimulertAFPOffentligJson = """{"simulertAFPOffentligBrutto":0,"tpi":0}"""
const val defaultInntektJson = """{"datoFom":"$defaultFomDateString","inntekt":0.0}"""

const val defaultSimuleringsDataJson = """{"datoFom":"1901-01-01","andvendtTrygdetid":0,"poengArTom1991":0,"poengArFom1992":0,"uforegradVedOmregning":0,"basisgp":0.0,"basispt":0.0,"basistp":0.0,"delingstallUttak":0.0,"forholdstallUttak":0.0,"sluttpoengtall":0.0}"""
const val defaultPensjonsbeholdningsperiodeJson = """{"datoFom":"1901-01-01","pensjonsbeholdning":0,"garantipensjonsbeholdning":0,"garantitilleggsbeholdning":0}"""
const val defaultTpForholdJson = """{"tpnr":"bogus","opptjeningsperiodeListe":$defaultOppjeningsperiodeListeJson}"""
const val defaultSimuleringsperiodeJson = """{"datoFom":"1901-01-01","folketrygdUttaksgrad":0,"stillingsprosentOffentlig":0,"simulerAFPOffentligEtterfulgtAvAlder":true}"""

const val defaultSimulerOffentligTjenestepensjonRequestJson = """{"fnr":"01010101010","fodselsdato":"19010101","sisteTpnr":"bogus","sprak":"bogus","simulertAFPOffentlig":$defaultSimulertAFPOffentligJson,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"sivilstandkode":"GIFT","inntektListe":[$defaultInntektJson],"pensjonsbeholdningsperiodeListe":[$defaultPensjonsbeholdningsperiodeJson],"simuleringsperiodeListe":[$defaultSimuleringsperiodeJson],"simuleringsdataListe":[$defaultSimuleringsDataJson],"tpForholdListe":[$defaultTpForholdJson]}"""