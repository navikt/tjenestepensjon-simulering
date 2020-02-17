package no.nav.tjenestepensjon.simulering.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.model.v1.request.Simuleringsdata
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = [
    "poengArTom1991",
    "poengArFom1992",
    "sluttpoengtall",
    "anvendtTrygdetid",
    "basisgp",
    "basistp",
    "basispt",
    "forholdstall_uttak",
    "skjermingstillegg",
    "uforegradVedOmregning"
])
class XMLSimuleringsdata {
    @XmlElement(required = true)
    var poengArTom1991: Int = 0
    @XmlElement(required = true)
    var poengArFom1992: Int = 0
    @XmlElement(required = true)
    var sluttpoengtall: Double = 0.0
    @XmlElement(required = true)
    var anvendtTrygdetid: Int = 0
    @XmlElement(required = false)
    var basisgp: Double? = null
    @XmlElement(required = false)
    var basistp: Double? = null
    @XmlElement(required = false)
    var basispt: Double? = null
    @XmlElement(required = true)
    var forholdstall_uttak: Double = 0.0
    @XmlElement(required = false)
    var skjermingstillegg: Double? = null
    @XmlElement(required = true)
    var uforegradVedOmregning: Int = 0


    fun toSimuleringsdata() = Simuleringsdata(
            poengArTom1991 = poengArTom1991,
            poengArFom1992 = poengArFom1992,
            sluttpoengtall = sluttpoengtall,
            anvendtTrygdetid = anvendtTrygdetid,
            basisgp = basisgp,
            basistp = basistp,
            basispt = basispt,
            forholdstall_uttak = forholdstall_uttak,
            skjermingstillegg = skjermingstillegg,
            uforegradVedOmregning = uforegradVedOmregning
    )
}