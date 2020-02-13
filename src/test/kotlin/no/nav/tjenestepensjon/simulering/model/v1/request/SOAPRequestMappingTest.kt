package no.nav.tjenestepensjon.simulering.model.v1.request

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TpForhold
import no.nav.tjenestepensjon.simulering.model.v1.soap.SOAPAdapter
import no.nav.tjenestepensjon.simulering.model.v1.soap.XMLHentStillingsprosentListeWrapper
import no.nav.tjenestepensjon.simulering.model.v1.soap.XMLSimulerOffentligTjenestePensjonWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.xml.transform.StringSource
import java.io.StringWriter
import java.time.LocalDate
import java.util.*
import javax.xml.transform.stream.StreamResult
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class SOAPRequestMappingTest {

    @Autowired
    lateinit var marshaller: Jaxb2Marshaller

    lateinit var writer: StringWriter
    lateinit var result: StreamResult

    @BeforeEach
    fun reset(){
        writer = StringWriter()
        result = StreamResult(writer)
    }

    @Test
    fun `Test marshalling of HentStillingsprosentListeRequest`(){
        marshaller.marshal(
                SOAPAdapter.marshal(defaultHentStillingsprosentListeRequest),
                result
        )
        val output = writer.toString()
        assertEquals(defaultHentStillingsprosentListeXML, output)
    }

    @Test
    fun `Test marshalling of SimulerOffentligTjenestepensjonRequest`(){
        marshaller.marshal(
                SOAPAdapter.marshal(defaultSimulerOffentligTjenestepensjonRequest),
                result
        )
        val output = writer.toString()
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestXML, output)
    }

    @Test
    fun `Test unmarshalling of HentStillingsprosentListeRequest`(){
        val wrapper = marshaller.unmarshal(StringSource(defaultHentStillingsprosentListeXML))
        val castWrapper = wrapper as XMLHentStillingsprosentListeWrapper
        val output = SOAPAdapter.unmarshal(castWrapper)
        assertEquals(defaultHentStillingsprosentListeRequest, output)
    }

    @Test
    fun `Test unmarshalling of SimulerOffentligTjenestepensjonRequest`(){
        val wrapper = marshaller.unmarshal(StringSource(defaultSimulerOffentligTjenestepensjonRequestXML)) as XMLSimulerOffentligTjenestePensjonWrapper
        val output = SOAPAdapter.unmarshal(wrapper)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequest, output)
    }

    companion object{
        private val defaultHentStillingsprosentListeRequest = HentStillingsprosentListeRequest(
                tssEksternId = "bogus",
                fnr = FNR("01010101010"),
                tpnr = "bogus",
                simuleringsKode = "AP"
        )

        private val defaultSimuleringsdata = Simuleringsdata(
                poengArTom1991 = 0,
                poengArFom1992 = 0,
                sluttpoengtall = 0.0,
                anvendtTrygdetid = 0,
                basisgp = 0.0,
                basistp = 0.0,
                basispt = 0.0,
                forholdstall_uttak = 0.0,
                skjermingstillegg = 0.0,
                uforegradVedOmregning = 0
        )

        private val defaultSimulertAP2011 = SimulertAP2011(
                simulertForsteuttak = defaultSimuleringsdata,
                simulertHeltUttakEtter67Ar = defaultSimuleringsdata
        )

        private val defaultStillingsprosent = Stillingsprosent(
                datoFom = LocalDate.of(2001,1,1),
                datoTom = LocalDate.of(2001,1,1),
                stillingsprosent = 0.0,
                aldersgrense = 0,
                faktiskHovedlonn = "bogus",
                stillingsuavhengigTilleggslonn = "bogus"
        )

        private val defaultTpForhold = TpForhold(
                tpnr = "bogus",
                tssEksternId = "bogus",
                stillingsprosentListe = Collections.singletonList(defaultStillingsprosent)
        )

        private val defaultSimulertAFPPrivat = SimulertAFPPrivat(
                afpOpptjeningTotalbelop = 0,
                kompensasjonstillegg = 0.0
        )

        private val defaultSimulerOffentligTjenestepensjonRequest = SimulerOffentligTjenestepensjonRequest(
                fnr = FNR("01010101010"),
                tpnr = "bogus",
                tssEksternId = "bogus",
                forsteUttakDato = LocalDate.of(2001, 1, 1),
                uttaksgrad = 0,
                heltUttakDato = LocalDate.of(2001, 1, 1),
                stillingsprosentOffHeltUttak = 0,
                stillingsprosentOffGradertUttak = 0,
                inntektForUttak = 0,
                inntektUnderGradertUttak = 0,
                inntektEtterHeltUttak = 0,
                antallArInntektEtterHeltUttak = 0,
                sivilstandKode = "bogus",
                sprak = "XML",
                simulertAFPOffentlig = 0,
                simulertAFPPrivat = defaultSimulertAFPPrivat,
                simulertAP2011 = defaultSimulertAP2011,
                tpForholdListe = Collections.singletonList(defaultTpForhold)
        )

        private const val defaultHentStillingsprosentListeXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ns2:hentStillingsprosentListe xmlns:ns2="http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1"><request><tssEksternId>bogus</tssEksternId><fnr>01010101010</fnr><tpnr>bogus</tpnr><simuleringsKode>AP</simuleringsKode></request></ns2:hentStillingsprosentListe>"""
        private const val defaultSimulerOffentligTjenestepensjonRequestXML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><ns2:simulerOffentligTjenestepensjon xmlns:ns2="http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1"><request><simulerTjenestepensjon><tpnr>bogus</tpnr><tssEksternId>bogus</tssEksternId><fnr>01010101010</fnr><forsteUttakDato>2001-01-01T00:00:00.000+01:00</forsteUttakDato><uttaksgrad>0</uttaksgrad><heltUttakDato>2001-01-01T00:00:00.000+01:00</heltUttakDato><stillingsprosentOffHeltUttak>0</stillingsprosentOffHeltUttak><stillingsprosentOffGradertUttak>0</stillingsprosentOffGradertUttak><inntektForUttak>0</inntektForUttak><inntektUnderGradertUttak>0</inntektUnderGradertUttak><inntektEtterHeltUttak>0</inntektEtterHeltUttak><antallArInntektEtterHeltUttak>0</antallArInntektEtterHeltUttak><sivilstandKode>bogus</sivilstandKode><sprak>XML</sprak><simulertAFPOffentlig>0</simulertAFPOffentlig><simulertAFPPrivat><afpOpptjeningTotalbelop>0</afpOpptjeningTotalbelop><kompensasjonstillegg>0.0</kompensasjonstillegg></simulertAFPPrivat><simulertAP2011><simulertForsteuttak><poengArTom1991>0</poengArTom1991><poengArFom1992>0</poengArFom1992><sluttpoengtall>0.0</sluttpoengtall><anvendtTrygdetid>0</anvendtTrygdetid><basisgp>0.0</basisgp><basistp>0.0</basistp><basispt>0.0</basispt><forholdstall_uttak>0.0</forholdstall_uttak><skjermingstillegg>0.0</skjermingstillegg><uforegradVedOmregning>0</uforegradVedOmregning></simulertForsteuttak><simulertHeltUttakEtter67ar><poengArTom1991>0</poengArTom1991><poengArFom1992>0</poengArFom1992><sluttpoengtall>0.0</sluttpoengtall><anvendtTrygdetid>0</anvendtTrygdetid><basisgp>0.0</basisgp><basistp>0.0</basistp><basispt>0.0</basispt><forholdstall_uttak>0.0</forholdstall_uttak><skjermingstillegg>0.0</skjermingstillegg><uforegradVedOmregning>0</uforegradVedOmregning></simulertHeltUttakEtter67ar></simulertAP2011><tpForholdListe><tpnr>bogus</tpnr><tssEksternId>bogus</tssEksternId><stillingsprosentListe><datoFom>2001-01-01T00:00:00.000+01:00</datoFom><datoTom>2001-01-01T00:00:00.000+01:00</datoTom><stillingsprosent>0.0</stillingsprosent><aldersgrense>0</aldersgrense><faktiskHovedlonn>bogus</faktiskHovedlonn><stillingsuavhengigTilleggslonn>bogus</stillingsuavhengigTilleggslonn></stillingsprosentListe></tpForholdListe></simulerTjenestepensjon></request></ns2:simulerOffentligTjenestepensjon>"""
    }
}