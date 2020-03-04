package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.v1.models.domain.TpForhold
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = [
    "tpnr",
    "tssEksternId",
    "stillingsprosentListe"
])
class XMLTpForhold {
    @XmlElement(required = true)
    lateinit var tpnr: String
    @XmlElement(required = true)
    lateinit var tssEksternId: String
    @XmlElement(required = false)
    var stillingsprosentListe: List<XMLStillingsprosent>? = null

    fun toTpForhold() = TpForhold(
            tpnr = tpnr,
            tssEksternId = tssEksternId,
            stillingsprosentListe = stillingsprosentListe?.map(XMLStillingsprosent::toStillingsprosent)
                    ?: emptyList()
    )
}