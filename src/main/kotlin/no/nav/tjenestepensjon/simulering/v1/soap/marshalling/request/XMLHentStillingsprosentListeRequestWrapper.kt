package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request

import jakarta.xml.bind.annotation.*
import jakarta.xml.bind.annotation.XmlAccessType.*

@XmlAccessorType(FIELD)
@XmlType(propOrder = ["request"])
@XmlRootElement(name = "hentStillingsprosentListe", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLHentStillingsprosentListeRequestWrapper {
    @XmlElement(required = true)
    lateinit var request: XMLHentStillingsprosentListeRequest

    @XmlAccessorType(FIELD)
    @XmlType(propOrder = [
        "tssEksternId",
        "fnr",
        "tpnr",
        "simuleringsKode"
    ])
    class XMLHentStillingsprosentListeRequest {
        @XmlElement(required = true)
        lateinit var tssEksternId: String
        @XmlElement(required = true)
        lateinit var fnr: String
        @XmlElement(required = true)
        lateinit var tpnr: String
        @XmlElement(required = true)
        lateinit var simuleringsKode: String
    }
}