package no.nav.tjenestepensjon.simulering.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.model.v1.request.SimulertAP2011
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = [
    "simulertForsteuttak",
    "simulertHeltUttakEtter67ar"
])
class XMLSimulertAP2011 {
    @XmlElement(required = true)
    lateinit var simulertForsteuttak: XMLSimuleringsdata
    @XmlElement(required = false)
    var simulertHeltUttakEtter67ar: XMLSimuleringsdata? = null

    fun toSimulertAP2011() = SimulertAP2011(
            simulertForsteuttak = simulertForsteuttak.toSimuleringsdata(),
            simulertHeltUttakEtter67Ar = simulertHeltUttakEtter67ar?.toSimuleringsdata()
    )
}