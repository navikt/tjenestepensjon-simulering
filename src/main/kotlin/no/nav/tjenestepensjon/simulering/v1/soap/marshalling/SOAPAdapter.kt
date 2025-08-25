package no.nav.tjenestepensjon.simulering.v1.soap.marshalling

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.XMLStillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper.XMLHentStillingsprosentListeResponse

object SOAPAdapter {

    private fun Stillingsprosent.toXML() = XMLStillingsprosent().also {
        it.aldersgrense = aldersgrense
        it.datoFom = datoFom.toXMLGregorianCalendar()
        it.datoTom = datoTom?.toXMLGregorianCalendar()
        it.faktiskHovedlonn = faktiskHovedlonn
        it.stillingsprosent = stillingsprosent
        it.stillingsuavhengigTilleggslonn = stillingsuavhengigTilleggslonn
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
                tssEksternId = this?.tssEksternId,
                fnr = FNR(this?.fnr),
                simuleringsKode = this?.simuleringsKode,
                tpnr = this?.tpnr
        )
    }

    fun marshal(p0: HentStillingsprosentListeResponse): XMLHentStillingsprosentListeResponseWrapper = with(p0) {
        XMLHentStillingsprosentListeResponseWrapper().also { wrapper ->
            wrapper.response = XMLHentStillingsprosentListeResponse().also {
                it.stillingsprosentListe = stillingsprosentListe.map { o -> o.toXML() }
            }
        }
    }

    fun unmarshal(p0: XMLHentStillingsprosentListeResponseWrapper): HentStillingsprosentListeResponse = with(p0.response) {
        HentStillingsprosentListeResponse(
                stillingsprosentListe = stillingsprosentListe.map(XMLStillingsprosent::toStillingsprosent)
        )
    }
}
