package no.nav.tjenestepensjon.simulering.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.soap.marshalling.Utvidelse.SimulertPensjonUtvidelse1
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = [
    "tpnr",
    "navnOrdning",
    "inkludertOrdningListe",
    "leverandorUrl",
    "utbetalingsperiodeListe",
    "utvidelse"
])
class XMLSimulertPensjon {
    @XmlElement(required = true)
    lateinit var tpnr: String
    @XmlElement(required = true)
    lateinit var navnOrdning: String
    @XmlElement(required = true)
    lateinit var inkludertOrdningListe: List<String>
    @XmlElement(required = true)
    lateinit var leverandorUrl: String
    @XmlElement(required = true, nillable = true)
    lateinit var utbetalingsperiodeListe: List<XMLUtbetalingsperiode?>
    lateinit var utvidelse: SimulertPensjonUtvidelse1

    fun toSimulertPensjon(fnr: FNR) = SimulertPensjon(
            tpnr = tpnr,
            navnOrdning = navnOrdning,
            inkluderteOrdninger = inkludertOrdningListe,
            leverandorUrl = leverandorUrl,
            utbetalingsperioder = utbetalingsperiodeListe.map { it?.toUtbetalingsperiode(fnr) }
    )
}