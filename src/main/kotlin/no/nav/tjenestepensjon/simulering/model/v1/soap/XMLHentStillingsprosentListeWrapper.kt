package no.nav.tjenestepensjon.simulering.model.v1.soap

import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = ["request"])
@XmlRootElement(name = "hentStillingsprosentListe", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLHentStillingsprosentListeWrapper {
    @XmlElement(required = true)
    lateinit var request: XMLHentStillingsprosentListeRequest

    @XmlAccessorType(XmlAccessType.FIELD)
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