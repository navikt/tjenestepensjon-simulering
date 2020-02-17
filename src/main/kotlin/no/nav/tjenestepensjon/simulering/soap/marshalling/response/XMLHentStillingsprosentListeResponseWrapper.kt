package no.nav.tjenestepensjon.simulering.soap.marshalling.response

import no.nav.tjenestepensjon.simulering.soap.marshalling.Utvidelse.HentStillingsprosentListeUtvidelse1
import no.nav.tjenestepensjon.simulering.soap.marshalling.domain.XMLStillingsprosent
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = ["response"])
@XmlRootElement(name = "hentStillingsprosentListeResponse", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLHentStillingsprosentListeResponseWrapper {
    @XmlElement(required = true)
    lateinit var response: XMLHentStillingsprosentListeResponse

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = ["stillingsprosentListe", "utvidelse"])
    class XMLHentStillingsprosentListeResponse {
        lateinit var stillingsprosentListe: List<XMLStillingsprosent>
        lateinit var utvidelse: HentStillingsprosentListeUtvidelse1
    }
}