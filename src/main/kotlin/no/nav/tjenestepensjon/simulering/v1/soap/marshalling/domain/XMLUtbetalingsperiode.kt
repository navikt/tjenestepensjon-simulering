package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.Utvidelse.UtbetalingsperiodeUtvidelse1
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = [
    "startAlder",
    "sluttAlder",
    "startManed",
    "sluttManed",
    "grad",
    "arligUtbetaling",
    "ytelseKode",
    "mangelfullSimuleringKode",
    "utvidelse"
])
class XMLUtbetalingsperiode {
    var startAlder: Int = 0
    var sluttAlder: Int = 0
    var startManed: Int = 0
    var sluttManed: Int = 0
    var grad: Int = 0
    var arligUtbetaling: Double = 0.0
    lateinit var ytelseKode: String
    var mangelfullSimuleringKode: String = ""
    lateinit var utvidelse: UtbetalingsperiodeUtvidelse1

    fun toUtbetalingsperiode(fnr: FNR) = Utbetalingsperiode(
            datoFom = fnr.datoAtAge(startAlder.toLong(), startManed.toLong(), false),
            datoTom = fnr.datoAtAge(sluttAlder.toLong(), sluttManed.toLong(), true),
            grad = grad,
            arligUtbetaling = arligUtbetaling,
            ytelsekode = ytelseKode,
            mangelfullSimuleringkode = mangelfullSimuleringKode
    )
}
