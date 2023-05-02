package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import jakarta.xml.bind.annotation.XmlAccessType.*
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlType
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.Utvidelse.SimulertPensjonUtvidelse1

@XmlAccessorType(FIELD)
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