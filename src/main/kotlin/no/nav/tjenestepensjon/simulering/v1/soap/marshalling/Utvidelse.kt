package no.nav.tjenestepensjon.simulering.v1.soap.marshalling

import jakarta.xml.bind.annotation.*
import jakarta.xml.bind.annotation.XmlAccessType.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.Utvidelse.*
import javax.xml.namespace.QName

@XmlAccessorType(FIELD)
@XmlType(name = "utvidelse", propOrder = ["any"])
@XmlSeeAlso(
        HentStillingsprosentListeUtvidelse1::class,
        StillingsprosentUtvidelse1::class,
        SimulerTjenestepensjonUtvidelse1::class,
        UtbetalingsperiodeUtvidelse1::class,
        SimulertPensjonUtvidelse1::class
)
abstract class Utvidelse {
    @XmlAnyElement(lax = true)
    lateinit var any: List<Any>
    @XmlAnyAttribute
    lateinit var otherAttributes: Map<QName, String>


    @XmlAccessorType(FIELD)
    @XmlType(name = "HentStillingsprosentListeUtvidelse1")
    class HentStillingsprosentListeUtvidelse1: Utvidelse()

    @XmlAccessorType(FIELD)
    @XmlType(name = "StillingsprosentUtvidelse1")
    class StillingsprosentUtvidelse1: Utvidelse()

    @XmlAccessorType(FIELD)
    @XmlType(name = "SimulerTjenestepensjonUtvidelse1")
    class SimulerTjenestepensjonUtvidelse1: Utvidelse()

    @XmlAccessorType(FIELD)
    @XmlType(name = "UtbetalingsperiodeUtvidelse1")
    class UtbetalingsperiodeUtvidelse1: Utvidelse()

    @XmlAccessorType(FIELD)
    @XmlType(name = "SimulertPensjonUtvidelse1")
    class SimulertPensjonUtvidelse1: Utvidelse()
}