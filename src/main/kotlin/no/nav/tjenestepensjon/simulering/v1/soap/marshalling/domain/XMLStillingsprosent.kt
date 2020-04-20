package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.Utvidelse
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.toLocalDate
import javax.xml.bind.annotation.*
import javax.xml.datatype.XMLGregorianCalendar

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = [
    "stillingsprosent",
    "datoFom",
    "datoTom",
    "faktiskHovedlonn",
    "stillingsuavhengigTilleggslonn",
    "aldersgrense",
    "utvidelse"
])
class XMLStillingsprosent {
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    lateinit var datoFom: XMLGregorianCalendar
    @XmlElement(required = false)
    @XmlSchemaType(name = "date")
    var datoTom: XMLGregorianCalendar? = null
    @XmlElement(required = true)
    var stillingsprosent: Double = 0.0
    @XmlElement(required = true)
    var aldersgrense: Int = 0
    @XmlElement(required = false)
    var faktiskHovedlonn: String? = null
    @XmlElement(required = false)
    var stillingsuavhengigTilleggslonn: String? = null
    @XmlElement(required = false)
    var utvidelse: Utvidelse.StillingsprosentUtvidelse1? = null

    fun toStillingsprosent() = Stillingsprosent(
            datoFom = datoFom.toLocalDate(),
            datoTom = datoTom?.toLocalDate(),
            stillingsprosent = stillingsprosent,
            aldersgrense = aldersgrense,
            faktiskHovedlonn = faktiskHovedlonn,
            stillingsuavhengigTilleggslonn = stillingsuavhengigTilleggslonn,
            utvidelse = null
    )
}