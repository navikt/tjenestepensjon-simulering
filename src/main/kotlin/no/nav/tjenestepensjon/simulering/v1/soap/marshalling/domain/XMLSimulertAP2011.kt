package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import jakarta.xml.bind.annotation.XmlAccessType.*
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlType
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulertAP2011

@XmlAccessorType(FIELD)
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