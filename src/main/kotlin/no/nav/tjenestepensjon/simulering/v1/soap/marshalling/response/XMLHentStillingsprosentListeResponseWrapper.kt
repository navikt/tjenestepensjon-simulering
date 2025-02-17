package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response

import jakarta.xml.bind.annotation.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.XMLStillingsprosent

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = ["response"])
@XmlRootElement(name = "hentStillingsprosentListeResponse", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLHentStillingsprosentListeResponseWrapper {
    @XmlElement(required = true)
    lateinit var response: XMLHentStillingsprosentListeResponse

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = ["stillingsprosentListe"])
    class XMLHentStillingsprosentListeResponse {
        lateinit var stillingsprosentListe: List<XMLStillingsprosent>
    }
}