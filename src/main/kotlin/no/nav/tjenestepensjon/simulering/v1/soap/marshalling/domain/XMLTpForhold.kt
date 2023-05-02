package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import jakarta.xml.bind.annotation.XmlAccessType.*
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlType
import no.nav.tjenestepensjon.simulering.v1.models.domain.TpForhold

@XmlAccessorType(FIELD)
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