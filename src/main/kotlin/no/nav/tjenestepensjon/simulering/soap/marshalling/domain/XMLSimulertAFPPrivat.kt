package no.nav.tjenestepensjon.simulering.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.model.v1.request.SimulertAFPPrivat
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
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