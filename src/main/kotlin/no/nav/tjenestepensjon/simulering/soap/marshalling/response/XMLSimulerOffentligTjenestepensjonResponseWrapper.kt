package no.nav.tjenestepensjon.simulering.soap.marshalling.response

import no.nav.tjenestepensjon.simulering.soap.marshalling.Utvidelse
import no.nav.tjenestepensjon.simulering.soap.marshalling.Utvidelse.HentStillingsprosentListeUtvidelse1
import no.nav.tjenestepensjon.simulering.soap.marshalling.domain.XMLSimulertPensjon
import no.nav.tjenestepensjon.simulering.soap.marshalling.domain.XMLStillingsprosent
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = ["response"])
@XmlRootElement(name = "simulerOffentligTjenestepensjonResponse", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLSimulerOffentligTjenestepensjonResponseWrapper {
    @XmlElement(required = true)
    lateinit var response: XMLSimulerOffentligTjenestepensjonResponse

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = ["simulertPensjonListe", "utvidelse"])
    class XMLSimulerOffentligTjenestepensjonResponse {
        lateinit var simulertPensjonListe: List<XMLSimulertPensjon>
        lateinit var utvidelse: Utvidelse.SimulerTjenestepensjonUtvidelse1
    }
}