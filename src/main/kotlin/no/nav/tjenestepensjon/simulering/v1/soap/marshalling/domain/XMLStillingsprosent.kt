package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain

import jakarta.xml.bind.annotation.XmlAccessType.FIELD
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlSchemaType
import jakarta.xml.bind.annotation.XmlType
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.toLocalDate
import javax.xml.datatype.XMLGregorianCalendar

@XmlAccessorType(FIELD)
@XmlType(propOrder = [
    "stillingsprosent",
    "datoFom",
    "datoTom",
    "faktiskHovedlonn",
    "stillingsuavhengigTilleggslonn",
    "aldersgrense"
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