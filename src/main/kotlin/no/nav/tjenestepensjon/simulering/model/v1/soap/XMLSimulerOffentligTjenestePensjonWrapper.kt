package no.nav.tjenestepensjon.simulering.model.v1.soap

import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TpForhold
import no.nav.tjenestepensjon.simulering.model.v1.request.Simuleringsdata
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulertAFPPrivat
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulertAP2011
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.XmlAccessType.FIELD
import javax.xml.datatype.XMLGregorianCalendar

@XmlAccessorType(FIELD)
@XmlType(propOrder = ["request"])
@XmlRootElement(name = "simulerOffentligTjenestepensjon", namespace = "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1")
class XMLSimulerOffentligTjenestePensjonWrapper {
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
        lateinit var forsteUttakDato: XMLGregorianCalendar
        @XmlElement(required = false)
        var uttaksgrad: Int? = null
        @XmlElement(required = false)
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
        lateinit var sprak: String
        @XmlElement(required = false)
        var simulertAFPOffentlig: Int? = null
        @XmlElement(required = false)
        var simulertAFPPrivat: XMLSimulertAFPPrivat? = null
        @XmlElement(required = true)
        lateinit var simulertAP2011: XMLSimulertAP2011
        @XmlElement(required = false)
        var tpForholdListe: List<XMLTpForhold>? = null
    }

    @XmlAccessorType(FIELD)
    @XmlType(propOrder = [
        "tpnr",
        "tssEksternId",
        "stillingsprosentListe"
    ])
    class XMLTpForhold {
        @XmlElement(required = true)
        lateinit var tpnr: String
        @XmlElement(required = true)
        lateinit var tssEksternId: String
        @XmlElement(required = false)
        var stillingsprosentListe: List<XMLStillingsprosent>? = null

        fun toTpForhold() = TpForhold(
                tpnr = tpnr,
                tssEksternId = tssEksternId,
                stillingsprosentListe = stillingsprosentListe?.map(XMLStillingsprosent::toStillingsprosent)
                        ?: emptyList()
        )
    }


    @XmlAccessorType(FIELD)
    @XmlType(propOrder = [
        "datoFom",
        "datoTom",
        "stillingsprosent",
        "aldersgrense",
        "faktiskHovedlonn",
        "stillingsuavhengigTilleggslonn"
    ])
    class XMLStillingsprosent {
        @XmlElement(required = true)
        lateinit var datoFom: XMLGregorianCalendar
        @XmlElement(required = false)
        var datoTom: XMLGregorianCalendar? = null
        @XmlElement(required = true)
        var stillingsprosent: Double = 0.0
        @XmlElement(required = true)
        var aldersgrense: Int = 0
        @XmlElement(required = true)
        lateinit var faktiskHovedlonn: String
        @XmlElement(required = true)
        lateinit var stillingsuavhengigTilleggslonn: String

        fun toStillingsprosent() = Stillingsprosent(
                datoFom = datoFom.toLocalDate(),
                datoTom = datoTom?.toLocalDate(),
                stillingsprosent = stillingsprosent,
                aldersgrense = aldersgrense,
                faktiskHovedlonn = faktiskHovedlonn,
                stillingsuavhengigTilleggslonn = stillingsuavhengigTilleggslonn
        )
    }


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


    @XmlAccessorType(FIELD)
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


    @XmlAccessorType(FIELD)
    @XmlType(propOrder = [
        "afpOpptjeningTotalbelop",
        "kompensasjonstillegg"
    ])
    class XMLSimulertAFPPrivat {
        @XmlElement(required = true)
        var afpOpptjeningTotalbelop: Int = 0
        @XmlElement(required = false)
        var kompensasjonstillegg: Double? = null

        fun toSimulertAFPPrivat() = SimulertAFPPrivat(
                afpOpptjeningTotalbelop = afpOpptjeningTotalbelop,
                kompensasjonstillegg = kompensasjonstillegg
        )
    }
}