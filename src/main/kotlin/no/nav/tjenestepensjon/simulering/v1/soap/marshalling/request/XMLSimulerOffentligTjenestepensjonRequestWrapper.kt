package no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request

import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.XMLSimulertAFPPrivat
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.XMLSimulertAP2011
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.XMLTpForhold
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.XmlAccessType.FIELD
import javax.xml.datatype.XMLGregorianCalendar

@XmlAccessorType(FIELD)
@XmlType(propOrder = ["request"])
@XmlRootElement(name = "simulerOffentligTjenestepensjon", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLSimulerOffentligTjenestepensjonRequestWrapper {
    @XmlElement(required = true)
    lateinit var request: WrapperTwoElectricBoogaloo

    @XmlAccessorType(FIELD)
    @XmlType(propOrder = ["simulerTjenestepensjon"])
    class WrapperTwoElectricBoogaloo {
        @XmlElement(required = true)
        lateinit var simulerTjenestepensjon: XMLSimulerOffentligTjenestepensjonRequest
    }

    @XmlAccessorType(FIELD)
    @XmlType(name = "simulerTjenestepensjon", propOrder = [
        "tpnr",
        "tssEksternId",
        "fnr",
        "forsteUttakDato",
        "uttaksgrad",
        "heltUttakDato",
        "stillingsprosentOffHeltUttak",
        "stillingsprosentOffGradertUttak",
        "inntektForUttak",
        "inntektUnderGradertUttak",
        "inntektEtterHeltUttak",
        "antallArInntektEtterHeltUttak",
        "sivilstandKode",
        "sprak",
        "simulertAFPOffentlig",
        "simulertAFPPrivat",
        "simulertAP2011",
        "tpForholdListe"
    ])
    class XMLSimulerOffentligTjenestepensjonRequest {
        @XmlElement(required = true)
        lateinit var tpnr: String
        @XmlElement(required = true)
        lateinit var tssEksternId: String
        @XmlElement(required = true)
        lateinit var fnr: String
        @XmlElement(required = true)
        @XmlSchemaType(name = "date")
        lateinit var forsteUttakDato: XMLGregorianCalendar
        @XmlElement(required = false)
        var uttaksgrad: Int? = null
        @XmlElement(required = false)
        @XmlSchemaType(name = "date")
        var heltUttakDato: XMLGregorianCalendar? = null
        @XmlElement(required = false)
        var stillingsprosentOffHeltUttak: Int? = null
        @XmlElement(required = false)
        var stillingsprosentOffGradertUttak: Int? = null
        @XmlElement(required = false)
        var inntektForUttak: Int? = null
        @XmlElement(required = false)
        var inntektUnderGradertUttak: Int? = null
        @XmlElement(required = false)
        var inntektEtterHeltUttak: Int? = null
        @XmlElement(required = false)
        var antallArInntektEtterHeltUttak: Int? = null
        @XmlElement(required = true)
        lateinit var sivilstandKode: String
        @XmlElement(required = true)
        var sprak: String? = "norsk"
        @XmlElement(required = false)
        var simulertAFPOffentlig: Int? = null
        @XmlElement(required = false)
        var simulertAFPPrivat: XMLSimulertAFPPrivat? = null
        @XmlElement(required = true)
        lateinit var simulertAP2011: XMLSimulertAP2011
        @XmlElement(required = false)
        var tpForholdListe: List<XMLTpForhold>? = null
    }


}