package no.nav.tjenestepensjon.simulering.v1.soap.marshalling

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.domain.TpForhold
import no.nav.tjenestepensjon.simulering.v1.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v1.models.request.*
import no.nav.tjenestepensjon.simulering.v1.models.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper.XMLHentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper.XMLSimulerOffentligTjenestepensjonResponse
import java.time.temporal.ChronoUnit.MONTHS

object SOAPAdapter {

    private fun SimulertAFPPrivat.toXML() = XMLSimulertAFPPrivat().also {
        it.afpOpptjeningTotalbelop = afpOpptjeningTotalbelop
        it.kompensasjonstillegg = kompensasjonstillegg
    }

    private fun SimulertAP2011.toXML() = XMLSimulertAP2011().also {
        it.simulertForsteuttak = simulertForsteuttak.toXML()
        it.simulertHeltUttakEtter67ar = simulertHeltUttakEtter67Ar?.toXML()
    }

    private fun Simuleringsdata.toXML() = XMLSimuleringsdata().also {
        it.anvendtTrygdetid = anvendtTrygdetid
        it.basisgp = basisgp
        it.basistp = basistp
        it.basispt = basispt
        it.forholdstall_uttak = forholdstall_uttak
        it.poengArFom1992 = poengArFom1992
        it.poengArTom1991 = poengArTom1991
        it.skjermingstillegg = skjermingstillegg
        it.sluttpoengtall = sluttpoengtall
        it.uforegradVedOmregning = uforegradVedOmregning
    }

    private fun TpForhold.toXML() = XMLTpForhold().also {
        it.stillingsprosentListe = stillingsprosentListe.takeUnless(List<Stillingsprosent>::isEmpty)?.map { it.toXML() }
        it.tpnr = tpnr
        it.tssEksternId = tssEksternId
    }

    private fun Stillingsprosent.toXML() = XMLStillingsprosent().also {
        it.aldersgrense = aldersgrense
        it.datoFom = datoFom.toXMLGregorianCalendar()
        it.datoTom = datoTom?.toXMLGregorianCalendar()
        it.faktiskHovedlonn = faktiskHovedlonn
        it.stillingsprosent = stillingsprosent
        it.stillingsuavhengigTilleggslonn = stillingsuavhengigTilleggslonn
    }

    private fun SimulertPensjon.toXML(fnr: FNR) = XMLSimulertPensjon().also {
        it.tpnr = tpnr!!
        it.navnOrdning = navnOrdning!!
        it.leverandorUrl = leverandorUrl!!
        it.inkludertOrdningListe = inkluderteOrdninger!!
        it.utbetalingsperiodeListe = utbetalingsperioder!!.map { it?.toXML(fnr) }
    }

    private fun Utbetalingsperiode.toXML(fnr: FNR) = XMLUtbetalingsperiode().also {
        val fomMonths = MONTHS.between(datoFom, fnr.birthDate).toInt()
        val tomMonths = MONTHS.between(datoTom, fnr.birthDate).toInt()
        it.arligUtbetaling = arligUtbetaling
        it.startAlder = fomMonths / 12
        it.sluttAlder = tomMonths / 12
        it.startManed = fomMonths % 12
        it.sluttManed = tomMonths % 12
        it.grad = grad
        it.mangelfullSimuleringKode = mangelfullSimuleringkode
        it.ytelseKode = ytelsekode
    }


    fun marshal(p0: HentStillingsprosentListeRequest): XMLHentStillingsprosentListeRequestWrapper = with(p0) {
        XMLHentStillingsprosentListeRequestWrapper().also { wrapper ->
            wrapper.request = XMLHentStillingsprosentListeRequestWrapper.XMLHentStillingsprosentListeRequest().also {
                it.tssEksternId = tssEksternId
                it.fnr = fnr.toString()
                it.simuleringsKode = simuleringsKode
                it.tpnr = tpnr
            }
        }
    }

    fun unmarshal(p0: XMLHentStillingsprosentListeRequestWrapper): HentStillingsprosentListeRequest = with(p0.request) {
        HentStillingsprosentListeRequest(
                tssEksternId = tssEksternId,
                fnr = FNR(fnr),
                simuleringsKode = simuleringsKode,
                tpnr = tpnr
        )
    }

    fun marshal(p0: SimulerOffentligTjenestepensjonRequest): XMLSimulerOffentligTjenestepensjonRequestWrapper = with(p0) {
        XMLSimulerOffentligTjenestepensjonRequestWrapper().also { wrapper ->
            wrapper.request = WrapperTwoElectricBoogaloo().also { wrapperTwo ->
                wrapperTwo.simulerTjenestepensjon = XMLSimulerOffentligTjenestepensjonRequest().also {
                    it.fnr = fnr.fnr
                    it.sprak = sprak
                    it.tpnr = tpnr
                    it.uttaksgrad = uttaksgrad
                    it.tssEksternId = tssEksternId
                    it.sivilstandKode = sivilstandKode
                    it.heltUttakDato = heltUttakDato?.toXMLGregorianCalendar()
                    it.forsteUttakDato = forsteUttakDato.toXMLGregorianCalendar()
                    it.simulertAFPPrivat = simulertAFPPrivat?.toXML()
                    it.simulertAFPOffentlig = simulertAFPOffentlig
                    it.stillingsprosentOffHeltUttak = stillingsprosentOffHeltUttak
                    it.inntektForUttak = inntektForUttak
                    it.stillingsprosentOffGradertUttak = stillingsprosentOffGradertUttak
                    it.inntektEtterHeltUttak = inntektEtterHeltUttak
                    it.simulertAP2011 = simulertAP2011.toXML()
                    it.inntektUnderGradertUttak = inntektUnderGradertUttak
                    it.antallArInntektEtterHeltUttak = antallArInntektEtterHeltUttak
                    it.tpForholdListe = tpForholdListe.takeUnless(List<TpForhold>::isEmpty)?.map { it.toXML() }
                }
            }
        }
    }

    fun unmarshal(p0: XMLSimulerOffentligTjenestepensjonRequestWrapper): SimulerOffentligTjenestepensjonRequest = with(p0.request.simulerTjenestepensjon) {
        SimulerOffentligTjenestepensjonRequest(
                fnr = FNR(fnr),
                sprak = sprak,
                tpnr = tpnr,
                uttaksgrad = uttaksgrad,
                tssEksternId = tssEksternId,
                sivilstandKode = sivilstandKode,
                heltUttakDato = heltUttakDato?.toLocalDate(),
                forsteUttakDato = forsteUttakDato.toLocalDate(),
                simulertAFPPrivat = simulertAFPPrivat?.toSimulertAFPPrivat(),
                simulertAFPOffentlig = simulertAFPOffentlig,
                stillingsprosentOffHeltUttak = stillingsprosentOffHeltUttak,
                inntektForUttak = inntektForUttak,
                stillingsprosentOffGradertUttak = stillingsprosentOffGradertUttak,
                inntektEtterHeltUttak = inntektEtterHeltUttak,
                simulertAP2011 = simulertAP2011.toSimulertAP2011(),
                inntektUnderGradertUttak = inntektUnderGradertUttak,
                antallArInntektEtterHeltUttak = antallArInntektEtterHeltUttak,
                tpForholdListe = tpForholdListe?.map(XMLTpForhold::toTpForhold) ?: emptyList()
        )
    }



    fun marshal(p0: HentStillingsprosentListeResponse): XMLHentStillingsprosentListeResponseWrapper = with(p0) {
        XMLHentStillingsprosentListeResponseWrapper().also { wrapper ->
            wrapper.response = XMLHentStillingsprosentListeResponse().also {
                it.stillingsprosentListe = stillingsprosentListe.map { it.toXML() }
            }
        }
    }

    fun unmarshal(p0: XMLHentStillingsprosentListeResponseWrapper): HentStillingsprosentListeResponse = with(p0.response) {
        HentStillingsprosentListeResponse(
                stillingsprosentListe = stillingsprosentListe.map(XMLStillingsprosent::toStillingsprosent)
        )
    }

    fun marshal(p0: SimulerOffentligTjenestepensjonResponse, fnr: FNR): XMLSimulerOffentligTjenestepensjonResponseWrapper = with(p0) {
        XMLSimulerOffentligTjenestepensjonResponseWrapper().also { wrapper ->
            wrapper.response = XMLSimulerOffentligTjenestepensjonResponse().also {
                it.simulertPensjonListe = simulertPensjonListe.map { it.toXML(fnr) }
            }
        }
    }

    fun unmarshal(p0: XMLSimulerOffentligTjenestepensjonResponseWrapper, fnr: FNR): SimulerOffentligTjenestepensjonResponse = with(p0.response) {
        SimulerOffentligTjenestepensjonResponse(
                simulertPensjonListe = simulertPensjonListe.map { it.toSimulertPensjon(fnr) }
        )
    }
}
