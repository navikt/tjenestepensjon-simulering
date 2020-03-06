package no.nav.tjenestepensjon.simulering.v2.models

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningperiode
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.domain.*
import no.nav.tjenestepensjon.simulering.v2.models.request.*
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
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
        ytelsekode = "bogus"
)

val defaultUtbetalingsperiodeListe = listOf(
        defaultUtbetalingsperiode,
        null
)

val defaultSimulertPensjon = SimulerOffentligTjenestepensjonResponse(
        tpnr = "bogus",
        navnOrdning = "bogus",
        inkluderteOrdningeListe = listOf("bogus"),
        leverandorUrl = "bogus",
        utbetalingsperiodeListe = defaultUtbetalingsperiodeListe
)

val defaultStillingsprosent = Opptjeningsperiode(
        aldersgrense = 0,
        datoFom = defaultDatoFom,
        datoTom = defaultDatoTom,
        stillingsprosent = 0.0,
        stillingsuavhengigTilleggslonn = "bogus",
        faktiskHovedlonn = "bogus"
)


val defaultHentStillingsprosentListeRequest = HentStillingsprosentListeRequest(
        tssEksternId = "bogus",
        fnr = defaultFNR,
        tpnr = "bogus",
        simuleringsKode = "bogus"
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
        uforegradVedOmregning = 0
)

val defaultSimulertAFPPrivat = SimulertAFPPrivat(
        afpOpptjeningTotalbelop = 0,
        kompensasjonstillegg = 0.0
)


val defaultSimulertAFPOffentlig = SimulertAFPOffentlig(
        simulertAFPOffentligBrutto = 0,
        tpi = 0.0
)


val defaultPensjonsbeholdningperiode = Pensjonsbeholdningperiode(
        datoFom = defaultDatoFom,
        pensjonsbeholdning = 0,
        garantipensjonsbeholdning = 0,
        garantilleggsbeholdning = 0
)

val defaultInntekt = Inntekt(
        datoFom = defaultDatoFom,
        inntekt = 0.0
)
//        var fodselsdato: String,
//        var sisteTpnr: String,
//        var sprak: String? = null,
//        var pensjonsbeholdningsperiodeListe: List<Pensjonsbeholdningsperiode> = emptyList(),
//        var simuleringsperiodeListe: List<Simuleringsperiode>,
//        var simuleringsdataListe: List<Simuleringsdata>,
//        var tpForholdListe: List<TpForhold>


val defaultSimulerPensjonRequest = SimulerPensjonRequest(
        fnr = defaultFNR,
        sivilstandKode = SivilstandCodeEnum.GIFT,
        sprak = "bogus",
        simuleringsperiodeListe = listOf(defaultSimuleringsperiode),
        simulertAFPOffentlig = defaultSimulertAFPOffentlig,
        simulertAFPPrivat = defaultSimulertAFPPrivat,
        pensjonsbeholdningsperiodeListe = listOf(defaultPensjonsbeholdningperiode),
        inntektListe = listOf(defaultInntekt)
)

val defaultSimuleringsdata = Simuleringsdata(
        poengArTom1991 = 0,
        poengArFom1992 = 0,
        sluttpoengtall = 0.0,
        basisgp = 0.0,
        basistp = 0.0,
        basispt = 0.0,
        uforegradVedOmregning = 0
)

val defaultTpForhold = TpForhold(
        tpnr = "bogus",
        opptjeningsperiodeListe = listOf(defaultStillingsprosent)
)


//        var fodselsdato: String,
//        var sisteTpnr: String,
//        var sivilstandCode: SivilstandCodeEnum,
//        var inntektListe: List<Inntekt>,
//        var pensjonsbeholdningsperiodeListe: List<Pensjonsbeholdningsperiode> = emptyList(),
//        var simuleringsperiodeListe: List<Simuleringsperiode>,
//        var simuleringsdataListe: List<Simuleringsdata>,


val defaultSimulerOffentligTjenestepensjonRequest = SimulerPensjonRequest(
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
        tpForholdListe = listOf(defaultTpForhold)
)

val defaultStillingsprosentListe: List<Opptjeningsperiode> = listOf(defaultStillingsprosent)

val defaultHentStillingsprosentListeResponse = HentStillingsprosentListeResponse(defaultStillingsprosentListe)

val defaultSimulerOffentligTjenestepensjonResponse = defaultSimulertPensjon

val defaultTPOrdning = TPOrdning("bogus", "bogus")

const val defaultFomDateString = "1901-01-01"
const val defaultTomDateString = "1901-01-31"

const val defaultSimulertPensjonJson = """{"tpnr":"bogus","navnOrdning":"bogus","inkluderteOrdninger":["bogus"],"leverandorUrl":"bogus","inkluderteTpnr":null,"utelatteTpnr":null,"utbetalingsperioder":[{"grad":0,"arligUtbetaling":0.0,"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","ytelsekode":"bogus","mangelfullSimuleringkode":"bogus"},null],"status":null,"feilkode":null,"feilbeskrivelse":null}"""
const val defaultSimulerOffentligTjenestepensjonResponseJson = """{"simulertPensjonListe":[$defaultSimulertPensjonJson]}"""
const val defaultHentStillingsprosentListeResponseJson = """[{"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"bogus"}]"""
const val defaultDelytelseJson = """{"pensjonstype":"BASISTP","belop":0.0}"""
const val defaultHentStillingsprosentListeRequestJson = """{"tssEksternId":"bogus","fnr":"$defaultFNRString","tpnr":"bogus","simuleringsKode":"bogus"}"""
const val defaultSimuleringsperiodeJson = """{"datoFom":"$defaultFomDateString","utg":0,"stillingsprosentOffentlig":0,"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"forholdstall":0.0,"delingstall":0.0,"uforegradVedOmregning":0,"delytelser":[$defaultDelytelseJson]}"""
const val defaultSimulertAFPPrivatJson = """{"afpOpptjeningTotalbelop":0,"kompensasjonstillegg":0.0}"""
const val defaultInntektJson = """{"datoFom":"$defaultFomDateString","inntekt":0.0}"""
const val defaultPensjonsbeholdningsperiodeJson = """{"datoFom":"$defaultFomDateString","pensjonsbeholdning":0,"garantipensjonsbeholdning":0,"garantilleggsbeholdning":0}"""
const val defaultSimulerPensjonRequestJson = """{"fnr":"$defaultFNRString","sivilstandkode":"bogus","sprak":"bogus","simuleringsperioder":[$defaultSimuleringsperiodeJson],"simulertAFPOffentlig":0,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"pensjonsbeholdningsperioder":[$defaultPensjonsbeholdningsperiodeJson],"inntekter":[$defaultInntektJson]}"""
const val defaultSimuleringsdataJson = """{"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"basisgp":0.0,"basistp":0.0,"basispt":0.0,"forholdstall_uttak":0.0,"skjermingstillegg":0.0,"uforegradVedOmregning":0}"""
const val defaultSimulertAP2011Json = """{"simulertForsteuttak":$defaultSimuleringsdataJson,"simulertHeltUttakEtter67Ar":$defaultSimuleringsdataJson}"""
const val defaultStillingsprosentJson = """{"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"bogus"}"""
const val defaultTpForholdJson = """{"tpnr":"bogus","tssEksternId":"bogus","stillingsprosentListe":[$defaultStillingsprosentJson]}"""
const val defaultSimulerOffentligTjenestepensjonRequestJson = """{"fnr":"$defaultFNRString","tpnr":"bogus","tssEksternId":"bogus","forsteUttakDato":"$defaultFomDateString","uttaksgrad":0,"heltUttakDato":"$defaultFomDateString","stillingsprosentOffHeltUttak":0,"stillingsprosentOffGradertUttak":0,"inntektForUttak":0,"inntektUnderGradertUttak":0,"inntektEtterHeltUttak":0,"antallArInntektEtterHeltUttak":0,"sivilstandKode":"bogus","sprak":"bogus","simulertAFPOffentlig":0,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"simulertAP2011":$defaultSimulertAP2011Json,"tpForholdListe":[$defaultTpForholdJson]}"""