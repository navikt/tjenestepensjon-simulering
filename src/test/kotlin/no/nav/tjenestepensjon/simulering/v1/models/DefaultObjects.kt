package no.nav.tjenestepensjon.simulering.v1.models

import no.nav.tjenestepensjon.simulering.*
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClientConfig.Companion.ENCODING

const val defaultLeverandor = "leverandor1"

val defaultStillingsprosent
    get() = Stillingsprosent(
        aldersgrense = 0,
        datoFom = defaultDatoFom,
        datoTom = defaultDatoTom,
        stillingsprosent = 0.0,
        stillingsuavhengigTilleggslonn = "bogus",
        faktiskHovedlonn = "bogus",
        utvidelse = null
    )

val defaultHentStillingsprosentListeRequest
    get() = HentStillingsprosentListeRequest(
        tssEksternId = defaultTssid, fnr = defaultFNR, tpnr = defaultTpid, simuleringsKode = "bogus"
    )

val defaultStillingsprosentListe
    get() = listOf(defaultStillingsprosent)

val defaultHentStillingsprosentListeResponse
    get() = HentStillingsprosentListeResponse(defaultStillingsprosentListe)

const val defaultDelytelseJson = """{"pensjonstype":"basistp","belop":0.0}"""
const val defaultSimuleringsperiodeJson =
    """{"datoFom":"$defaultFomDateString","utg":0,"stillingsprosentOffentlig":0,"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"forholdstall":0.0,"delingstall":0.0,"uforegradVedOmregning":0,"delytelser":[$defaultDelytelseJson]}"""
const val defaultSimulertAFPPrivatJson = """{"afpOpptjeningTotalbelop":0,"kompensasjonstillegg":0.0}"""
const val defaultInntektJson = """{"datoFom":"$defaultFomDateString","inntekt":0.0}"""
const val defaultPensjonsbeholdningsperiodeJson =
    """{"datoFom":"$defaultFomDateString","pensjonsbeholdning":0,"garantipensjonsbeholdning":0,"garantitilleggsbeholdning":0}"""
const val defaultSimulerPensjonRequestJson =
    """{"fnr":"$defaultFNRString","fodselsdato":"1958-10-01","sivilstandkode":"GIFT","sprak":"bogus","simuleringsperioder":[$defaultSimuleringsperiodeJson],"simulertAFPOffentlig":null,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"pensjonsbeholdningsperioder":[$defaultPensjonsbeholdningsperiodeJson],"inntekter":[$defaultInntektJson]}"""
const val defaultSimuleringsdataJson =
    """{"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"basisgp":0.0,"basistp":0.0,"basispt":0.0,"forholdstall_uttak":0.0,"skjermingstillegg":0.0,"uforegradVedOmregning":0}"""
const val defaultSimulertAP2011Json =
    """{"simulertForsteuttak":$defaultSimuleringsdataJson,"simulertHeltUttakEtter67Ar":$defaultSimuleringsdataJson}"""
const val defaultStillingsprosentJson =
    """{"datoFom":"$defaultFomDateString","datoTom":"$defaultTomDateString","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"bogus"}"""
const val defaultTpForholdJson =
    """{"tpnr":"$defaultTpid","tssEksternId":$defaultTssid","stillingsprosentListe":[$defaultStillingsprosentJson]}"""

const val defaultXMLMetadata = """<?xml version="1.0" encoding="$ENCODING" standalone="yes"?>"""
const val defaultPackageXML = """xmlns:ns2="http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1"""
const val defaultFomDateXML = """$defaultFomDateString+01:00"""
const val defaultTomDateXML = """$defaultTomDateString+01:00"""

const val defaultHentStillingsprosentListeRequestXML =
    """$defaultXMLMetadata<ns2:hentStillingsprosentListe $defaultPackageXML"><request><tssEksternId>$defaultTssid</tssEksternId><fnr>$defaultFNRString</fnr><tpnr>$defaultTpid</tpnr><simuleringsKode>bogus</simuleringsKode></request></ns2:hentStillingsprosentListe>"""
const val defaultHentStillingsprosentListeResponseXML =
    """$defaultXMLMetadata<ns2:hentStillingsprosentListeResponse $defaultPackageXML"><response><stillingsprosentListe><stillingsprosent>0.0</stillingsprosent><datoFom>$defaultFomDateXML</datoFom><datoTom>$defaultTomDateXML</datoTom><faktiskHovedlonn>bogus</faktiskHovedlonn><stillingsuavhengigTilleggslonn>bogus</stillingsuavhengigTilleggslonn><aldersgrense>0</aldersgrense></stillingsprosentListe></response></ns2:hentStillingsprosentListeResponse>"""
