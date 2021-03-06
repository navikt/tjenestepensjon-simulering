package no.nav.tjenestepensjon.simulering.v1.models

import no.nav.tjenestepensjon.simulering.domain.DelytelseType.BASISTP
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.models.domain.*
import no.nav.tjenestepensjon.simulering.v1.models.request.*
import no.nav.tjenestepensjon.simulering.v1.models.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import java.time.LocalDate


const val defaultFNRString = "01010101010"

val defaultFNR = FNR(defaultFNRString)

val defaultDatoFom: LocalDate = LocalDate.of(1901, 1, 1)
val defaultDatoTom: LocalDate = LocalDate.of(1901, 1, 31)

val defaultUtbetalingsperiode = Utbetalingsperiode(
        grad = 0,
        arligUtbetaling = 0.0,
        datoFom = defaultDatoFom,
        datoTom = defaultDatoTom,
        ytelsekode = "bogus",
        mangelfullSimuleringkode = "bogus"
)

val defaultUtbetalingsperiodeListe = listOf(
        defaultUtbetalingsperiode,
        null
)

val defaultSimulertPensjon = SimulertPensjon(
        tpnr = "bogus",
        navnOrdning = "bogus",
        inkluderteOrdninger = listOf("bogus"),
        leverandorUrl = "bogus",
        utbetalingsperioder = defaultUtbetalingsperiodeListe
)

val defaultSimulertPensjonList = listOf(defaultSimulertPensjon)

val defaultStillingsprosent = Stillingsprosent(
        aldersgrense = 0,
        datoFom = defaultDatoFom,
        datoTom = defaultDatoTom,
        stillingsprosent = 0.0,
        stillingsuavhengigTilleggslonn = "bogus",
        faktiskHovedlonn = "bogus",
        utvidelse = null
)


val defaultHentStillingsprosentListeRequest = HentStillingsprosentListeRequest(
        tssEksternId = "bogus",
        fnr = defaultFNR,
        tpnr = "bogus",
        simuleringsKode = "bogus"
)

val defaultDelytelse = Delytelse(
        pensjonstype = BASISTP,
        belop = 0.0
)

val defaultSimuleringsperiode = Simuleringsperiode(
        datoFom = defaultDatoFom,
        utg = 0,
        stillingsprosentOffentlig = 0,
        poengArTom1991 = 0,
        poengArFom1992 = 0,
        sluttpoengtall = 0.0,
        anvendtTrygdetid = 0,
        forholdstall = 0.0,
        delingstall = 0.0,
        uforegradVedOmregning = 0,
        delytelser = listOf(defaultDelytelse)
)

val defaultSimulertAFPPrivat = SimulertAFPPrivat(
        afpOpptjeningTotalbelop = 0,
        kompensasjonstillegg = 0.0
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

val defaultSimulerPensjonRequest = SimulerPensjonRequest(
        fnr = defaultFNR,
        sivilstandkode = "bogus",
        sprak = "bogus",
        simuleringsperioder = listOf(defaultSimuleringsperiode),
        simulertAFPOffentlig = 0,
        simulertAFPPrivat = defaultSimulertAFPPrivat,
        pensjonsbeholdningsperioder = listOf(defaultPensjonsbeholdningperiode),
        inntekter = listOf(defaultInntekt)
)

val defaultSimuleringsdata = Simuleringsdata(
        poengArTom1991 = 0,
        poengArFom1992 = 0,
        sluttpoengtall = 0.0,
        anvendtTrygdetid = 0,
        basisgp = 0.0,
        basistp = 0.0,
        basispt = 0.0,
        forholdstall_uttak = 0.0,
        skjermingstillegg = 0.0,
        uforegradVedOmregning = 0
)

val defaultSimulertAP2011 = SimulertAP2011(
        simulertForsteuttak = defaultSimuleringsdata,
        simulertHeltUttakEtter67Ar = defaultSimuleringsdata
)


val defaultTpForhold = TpForhold(
        tpnr = "bogus",
        tssEksternId = "bogus",
        stillingsprosentListe = listOf(defaultStillingsprosent)
)

val defaultSimulerOffentligTjenestepensjonRequest = SimulerOffentligTjenestepensjonRequest(
        fnr = defaultFNR,
        tpnr = "bogus",
        tssEksternId = "bogus",
        forsteUttakDato = defaultDatoFom,
        uttaksgrad = 0,
        heltUttakDato = defaultDatoFom,
        stillingsprosentOffHeltUttak = 0,
        stillingsprosentOffGradertUttak = 0,
        inntektForUttak = 0,
        inntektUnderGradertUttak = 0,
        inntektEtterHeltUttak = 0,
        antallArInntektEtterHeltUttak = 0,
        sivilstandKode = "bogus",
        sprak = "bogus",
        simulertAFPOffentlig = 0,
        simulertAFPPrivat = defaultSimulertAFPPrivat,
        simulertAP2011 = defaultSimulertAP2011,
        tpForholdListe = listOf(defaultTpForhold)
)

val defaultStillingsprosentListe: List<Stillingsprosent> = listOf(defaultStillingsprosent)

val defaultHentStillingsprosentListeResponse = HentStillingsprosentListeResponse(defaultStillingsprosentListe)

val defaultSimulerOffentligTjenestepensjonResponse = SimulerOffentligTjenestepensjonResponse(defaultSimulertPensjonList)

val defaultTPOrdning = TPOrdning("bogus", "bogus")

const val defaultFomDateString = "1901-01-01"
const val defaultTomDateString = "1901-01-31"

const val defaultSimulertPensjonJson = """{"tpnr":"bogus","navnOrdning":"bogus","inkluderteOrdninger":["bogus"],"leverandorUrl":"bogus","inkluderteTpnr":null,"utelatteTpnr":null,"utbetalingsperioder":[{"grad":0,"arligUtbetaling":0.0,"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","ytelsekode":"bogus","mangelfullSimuleringkode":"bogus"},null],"status":null,"feilkode":null,"feilbeskrivelse":null}"""
const val defaultSimulerOffentligTjenestepensjonResponseJson = """{"simulertPensjonListe":[$defaultSimulertPensjonJson]}"""
const val defaultHentStillingsprosentListeResponseJson = """[{"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"bogus"}]"""
const val defaultDelytelseJson = """{"pensjonstype":"basistp","belop":0.0}"""
const val defaultHentStillingsprosentListeRequestJson = """{"tssEksternId":"bogus","fnr":"$defaultFNRString","tpnr":"bogus","simuleringsKode":"bogus"}"""
const val defaultSimuleringsperiodeJson = """{"datoFom":"$defaultFomDateString","utg":0,"stillingsprosentOffentlig":0,"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"forholdstall":0.0,"delingstall":0.0,"uforegradVedOmregning":0,"delytelser":[$defaultDelytelseJson]}"""
const val defaultSimulertAFPPrivatJson = """{"afpOpptjeningTotalbelop":0,"kompensasjonstillegg":0.0}"""
const val defaultInntektJson = """{"datoFom":"$defaultFomDateString","inntekt":0.0}"""
const val defaultPensjonsbeholdningsperiodeJson = """{"datoFom":"$defaultFomDateString","pensjonsbeholdning":0,"garantipensjonsbeholdning":0,"garantitilleggsbeholdning":0}"""
const val defaultSimulerPensjonRequestJson = """{"fnr":"$defaultFNRString","sivilstandkode":"bogus","sprak":"bogus","simuleringsperioder":[$defaultSimuleringsperiodeJson],"simulertAFPOffentlig":0,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"pensjonsbeholdningsperioder":[$defaultPensjonsbeholdningsperiodeJson],"inntekter":[$defaultInntektJson]}"""
const val defaultSimuleringsdataJson = """{"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"basisgp":0.0,"basistp":0.0,"basispt":0.0,"forholdstall_uttak":0.0,"skjermingstillegg":0.0,"uforegradVedOmregning":0}"""
const val defaultSimulertAP2011Json = """{"simulertForsteuttak":$defaultSimuleringsdataJson,"simulertHeltUttakEtter67Ar":$defaultSimuleringsdataJson}"""
const val defaultStillingsprosentJson = """{"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"bogus"}"""
const val defaultTpForholdJson = """{"tpnr":"bogus","tssEksternId":"bogus","stillingsprosentListe":[$defaultStillingsprosentJson]}"""
const val defaultSimulerOffentligTjenestepensjonRequestJson = """{"fnr":"$defaultFNRString","tpnr":"bogus","tssEksternId":"bogus","forsteUttakDato":"$defaultFomDateString","uttaksgrad":0,"heltUttakDato":"$defaultFomDateString","stillingsprosentOffHeltUttak":0,"stillingsprosentOffGradertUttak":0,"inntektForUttak":0,"inntektUnderGradertUttak":0,"inntektEtterHeltUttak":0,"antallArInntektEtterHeltUttak":0,"sivilstandKode":"bogus","sprak":"bogus","simulertAFPOffentlig":0,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"simulertAP2011":$defaultSimulertAP2011Json,"tpForholdListe":[$defaultTpForholdJson]}"""

const val defaultXMLMetadata = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>"""
const val defaultPackageXML = """xmlns:ns2="http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1"""
const val defaultFomDateXML = """$defaultFomDateString+01:00"""
const val defaultTomDateXML = """$defaultTomDateString+01:00"""

const val defaultSimulertAFPPrivatXML = """<afpOpptjeningTotalbelop>0</afpOpptjeningTotalbelop><kompensasjonstillegg>0.0</kompensasjonstillegg>"""
const val defaultStillingsprosentXML = """<stillingsprosent>0.0</stillingsprosent><datoFom>$defaultFomDateXML</datoFom><datoTom>$defaultTomDateXML</datoTom><faktiskHovedlonn>bogus</faktiskHovedlonn><stillingsuavhengigTilleggslonn>bogus</stillingsuavhengigTilleggslonn><aldersgrense>0</aldersgrense>"""
const val defaultTPForholdXML = """<tpnr>bogus</tpnr><tssEksternId>bogus</tssEksternId><stillingsprosentListe>$defaultStillingsprosentXML</stillingsprosentListe>"""
const val defaultSimulertUttakXML = """<poengArTom1991>0</poengArTom1991><poengArFom1992>0</poengArFom1992><sluttpoengtall>0.0</sluttpoengtall><anvendtTrygdetid>0</anvendtTrygdetid><basisgp>0.0</basisgp><basistp>0.0</basistp><basispt>0.0</basispt><forholdstall_uttak>0.0</forholdstall_uttak><skjermingstillegg>0.0</skjermingstillegg><uforegradVedOmregning>0</uforegradVedOmregning>"""
const val defaultSimulertAP2011XML = """<simulertForsteuttak>$defaultSimulertUttakXML</simulertForsteuttak><simulertHeltUttakEtter67ar>$defaultSimulertUttakXML</simulertHeltUttakEtter67ar>"""
const val defaultHentStillingsprosentListeRequestXML = """$defaultXMLMetadata<ns2:hentStillingsprosentListe $defaultPackageXML"><request><tssEksternId>bogus</tssEksternId><fnr>$defaultFNRString</fnr><tpnr>bogus</tpnr><simuleringsKode>bogus</simuleringsKode></request></ns2:hentStillingsprosentListe>"""
const val defaultSimulerOffentligTjenestepensjonRequestXML = """$defaultXMLMetadata<ns2:simulerOffentligTjenestepensjon $defaultPackageXML"><request><simulerTjenestepensjon><tpnr>bogus</tpnr><tssEksternId>bogus</tssEksternId><fnr>$defaultFNRString</fnr><forsteUttakDato>$defaultFomDateXML</forsteUttakDato><uttaksgrad>0</uttaksgrad><heltUttakDato>$defaultFomDateXML</heltUttakDato><stillingsprosentOffHeltUttak>0</stillingsprosentOffHeltUttak><stillingsprosentOffGradertUttak>0</stillingsprosentOffGradertUttak><inntektForUttak>0</inntektForUttak><inntektUnderGradertUttak>0</inntektUnderGradertUttak><inntektEtterHeltUttak>0</inntektEtterHeltUttak><antallArInntektEtterHeltUttak>0</antallArInntektEtterHeltUttak><sivilstandKode>bogus</sivilstandKode><sprak>bogus</sprak><simulertAFPOffentlig>0</simulertAFPOffentlig><simulertAFPPrivat>$defaultSimulertAFPPrivatXML</simulertAFPPrivat><simulertAP2011>$defaultSimulertAP2011XML</simulertAP2011><tpForholdListe>$defaultTPForholdXML</tpForholdListe></simulerTjenestepensjon></request></ns2:simulerOffentligTjenestepensjon>"""
const val defaultHentStillingsprosentListeResponseXML = """$defaultXMLMetadata<ns2:hentStillingsprosentListeResponse $defaultPackageXML"><response><stillingsprosentListe><stillingsprosent>0.0</stillingsprosent><datoFom>$defaultFomDateXML</datoFom><datoTom>$defaultTomDateXML</datoTom><faktiskHovedlonn>bogus</faktiskHovedlonn><stillingsuavhengigTilleggslonn>bogus</stillingsuavhengigTilleggslonn><aldersgrense>0</aldersgrense></stillingsprosentListe></response></ns2:hentStillingsprosentListeResponse>"""
const val defaultSimulerOffentligTjenestepensjonResponseXML = """$defaultXMLMetadata<ns2:simulerOffentligTjenestepensjonResponse $defaultPackageXML"><response><simulertPensjonListe><tpnr>bogus</tpnr><navnOrdning>bogus</navnOrdning><inkludertOrdningListe>bogus</inkludertOrdningListe><leverandorUrl>bogus</leverandorUrl><utbetalingsperiodeListe><startAlder>0</startAlder><sluttAlder>0</sluttAlder><startManed>0</startManed><sluttManed>0</sluttManed><grad>0</grad><arligUtbetaling>0.0</arligUtbetaling><ytelseKode>bogus</ytelseKode><mangelfullSimuleringKode>bogus</mangelfullSimuleringKode></utbetalingsperiodeListe><utbetalingsperiodeListe xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/></simulertPensjonListe></response></ns2:simulerOffentligTjenestepensjonResponse>"""
