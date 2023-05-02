package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import jakarta.xml.bind.annotation.XmlAccessType.*
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlType
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulertAFPPrivat

@XmlAccessorType(FIELD)
@XmlType(propOrder = [
    "afpOpptjeningTotalbelop",
    "kompensasjonstillegg"
])
class XMLSimulertAFPPrivat {
    @XmlElement(required = true)
    var afpOpptjeningTotalbelop: Int = 0
    @XmlElement(required = false)
    var kompensasjonstillegg: Double? = null

    fun toSimulertAFPPrivat() = SimulertAFPPrivat(
            afpOpptjeningTotalbelop = afpOpptjeningTotalbelop,
            kompensasjonstillegg = kompensasjonstillegg
    )
}