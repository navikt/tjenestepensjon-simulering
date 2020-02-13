package no.nav.tjenestepensjon.simulering.model.v1.soap

import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TpForhold
import no.nav.tjenestepensjon.simulering.model.v1.request.*
import no.nav.tjenestepensjon.simulering.model.v1.soap.XMLSimulerOffentligTjenestePensjonWrapper.*

object SOAPAdapter {

    fun SimulertAFPPrivat.toXML() = XMLSimulertAFPPrivat().also {
        it.afpOpptjeningTotalbelop = afpOpptjeningTotalbelop
        it.kompensasjonstillegg = kompensasjonstillegg
    }

    fun SimulertAP2011.toXML() = XMLSimulertAP2011().also {
        it.simulertForsteuttak = simulertForsteuttak.toXML()
        it.simulertHeltUttakEtter67ar = simulertHeltUttakEtter67Ar?.toXML()
    }

    fun Simuleringsdata.toXML() = XMLSimuleringsdata().also {
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

    fun TpForhold.toXML() = XMLTpForhold().also {
        it.stillingsprosentListe = stillingsprosentListe.takeUnless(List<Stillingsprosent>::isEmpty)?.map { it.toXML() }
        it.tpnr = tpnr
        it.tssEksternId = tssEksternId
    }

    fun Stillingsprosent.toXML() = XMLStillingsprosent().also {
        it.aldersgrense = aldersgrense
        it.datoFom = datoFom.toXMLGregorianCalendar()
        it.datoTom = datoTom?.toXMLGregorianCalendar()
        it.faktiskHovedlonn = faktiskHovedlonn
        it.stillingsprosent = stillingsprosent
        it.stillingsuavhengigTilleggslonn = stillingsuavhengigTilleggslonn
    }


    fun marshal(p0: HentStillingsprosentListeRequest): XMLHentStillingsprosentListeWrapper = with(p0) {
        XMLHentStillingsprosentListeWrapper().also { wrapper ->
            wrapper.request = XMLHentStillingsprosentListeWrapper.XMLHentStillingsprosentListeRequest().also {
                it.tssEksternId = tssEksternId
                it.fnr = fnr.toString()
                it.simuleringsKode = simuleringsKode
                it.tpnr = tpnr
            }
        }
    }

    fun unmarshal(p0: XMLHentStillingsprosentListeWrapper): HentStillingsprosentListeRequest = with(p0.request) {
        HentStillingsprosentListeRequest(
                tssEksternId = tssEksternId,
                fnr = FNR(fnr),
                simuleringsKode = simuleringsKode,
                tpnr = tpnr
        )
    }

    fun marshal(p0: SimulerOffentligTjenestepensjonRequest): XMLSimulerOffentligTjenestePensjonWrapper = with(p0) {
        XMLSimulerOffentligTjenestePensjonWrapper().also { wrapper ->
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

    fun unmarshal(p0: XMLSimulerOffentligTjenestePensjonWrapper): SimulerOffentligTjenestepensjonRequest = with(p0.request.simulerTjenestepensjon) {
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

}