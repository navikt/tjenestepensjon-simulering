package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request

import jakarta.xml.bind.annotation.*
import jakarta.xml.bind.annotation.XmlAccessType.*

@XmlAccessorType(FIELD)
@XmlType(propOrder = ["request"])
@XmlRootElement(name = "hentStillingsprosentListe", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
data class XMLHentStillingsprosentListeRequestWrapper(
    @field:XmlElement(required = true)
    var request: XMLHentStillingsprosentListeRequest? = null
){
    @XmlAccessorType(FIELD)
    @XmlType(propOrder = [
        "tssEksternId",
        "fnr",
        "tpnr",
        "simuleringsKode"
    ])
    data class XMLHentStillingsprosentListeRequest(
        @field:XmlElement(required = true)
        var tssEksternId: String? = null,
        @field:XmlElement(required = true)
        var fnr: String? = null,
        @field:XmlElement(required = true)
        var tpnr: String? = null,
        @field:XmlElement(required = true)
        var simuleringsKode: String? = null
    )
}