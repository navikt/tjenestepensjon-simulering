package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.domain.DelytelseType.BASISTP
import no.nav.tjenestepensjon.simulering.model.v1.domain.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.util.Collections.singletonList
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTRequestMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test serializing of HentStillingsprosentListeRequest`() {
        val result = objectMapper.writeValueAsString(defaultHentStillingsprosentListeRequest)
        assertEquals(defaultHentStillingsprosentListeRequestJson, result)
    }

    @Test
    fun `Test serializing of SimulerPensjonsRequest`() {
        val result = objectMapper.writeValueAsString(defaultSimulerPensjonRequest)
        assertEquals(defaultSimulerPensjonRequestJson, result)
    }

    @Test
    fun `Test serializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonRequest)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestJson, result)
    }

    @Test
    fun `Test deserializing of HentStillingsprosentListeRequest`() {
        val result = objectMapper.readValue(defaultHentStillingsprosentListeRequestJson, HentStillingsprosentListeRequest::class.java)
        assertEquals(defaultHentStillingsprosentListeRequest, result)
    }

    @Test
    fun `Test deserializing of SimulerPensjonsRequest`() {
        val result = objectMapper.readValue(defaultSimulerPensjonRequestJson, SimulerPensjonRequest::class.java)
        assertEquals(defaultSimulerPensjonRequest, result)
        val minimalResult = objectMapper.readValue(minimalSimulerPensjonRequestJson, SimulerPensjonRequest::class.java)
        assertEquals(minimalSimulerPensjonRequest, minimalResult)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonRequestJson, SimulerOffentligTjenestepensjonRequest::class.java)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequest, result)
    }

    companion object {
        private val defaultHentStillingsprosentListeRequest = HentStillingsprosentListeRequest(
                tssEksternId = "bogus",
                fnr = FNR("01010101010"),
                tpnr = "bogus",
                simuleringsKode = "AP"
        )

        private val defaultDelytelse = Delytelse(
                pensjonstype = BASISTP,
                belop = 0.0
        )

        private val defaultSimuleringsperiode = Simuleringsperiode(
                datoFom = LocalDate.of(2001, 1, 1),
                utg = 0,
                stillingsprosentOffentlig = 0,
                poengArTom1991 = 0,
                poengArFom1992 = 0,
                sluttpoengtall = 0.0,
                anvendtTrygdetid = 0,
                forholdstall = 0.0,
                delingstall = 0.0,
                uforegradVedOmregning = 0,
                delytelser = singletonList(defaultDelytelse)
        )

        private val defaultSimulertAFPPrivat = SimulertAFPPrivat(
                afpOpptjeningTotalbelop = 0,
                kompensasjonstillegg = null
        )

        private val defaultPensjonsbeholdningperiode = Pensjonsbeholdningperiode(
                datoFom = LocalDate.of(2001, 1, 1),
                pensjonsbeholdning = 0,
                garantipensjonsbeholdning = 0,
                garantilleggsbeholdning = 0
        )

        private val defaultInntekt = Inntekt(
                datoFom = LocalDate.of(2001, 1, 1),
                inntekt = 0.0
        )

        private val defaultSimulerPensjonRequest = SimulerPensjonRequest(
                fnr = FNR("01010101010"),
                sivilstandkode = "bogus",
                sprak = "json",
                simuleringsperioder = singletonList(defaultSimuleringsperiode),
                simulertAFPOffentlig = null,
                simulertAFPPrivat = defaultSimulertAFPPrivat,
                pensjonsbeholdningsperioder = singletonList(defaultPensjonsbeholdningperiode),
                inntekter = singletonList(defaultInntekt)
        )

        private val minimalSimulerPensjonRequest = SimulerPensjonRequest(
                fnr = FNR("01010101010"),
                sivilstandkode = "bogus",
                simuleringsperioder = emptyList(),
                inntekter = emptyList()
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
                stillingsprosentListe = singletonList(defaultStillingsprosent)
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
                sprak = "json",
                simulertAFPOffentlig = 0,
                simulertAFPPrivat = defaultSimulertAFPPrivat,
                simulertAP2011 = defaultSimulertAP2011,
                tpForholdListe = singletonList(defaultTpForhold)
        )


        private const val defaultHentStillingsprosentListeRequestJson = """{"tssEksternId":"bogus","fnr":"01010101010","tpnr":"bogus","simuleringsKode":"AP"}"""
        private const val defaultDelytelseJson = """{"pensjonstype":"BASISTP","belop":0.0}"""
        private const val defaultSimuleringsperiodeJson = """{"datoFom":"2001-01-01","utg":0,"stillingsprosentOffentlig":0,"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"forholdstall":0.0,"delingstall":0.0,"uforegradVedOmregning":0,"delytelser":[$defaultDelytelseJson]}"""
        private const val defaultSimulertAFPPrivatJson = """{"afpOpptjeningTotalbelop":0,"kompensasjonstillegg":null}"""
        private const val defaultPensjonsbeholdningsperiodeJson = """{"datoFom":"2001-01-01","pensjonsbeholdning":0,"garantipensjonsbeholdning":0,"garantilleggsbeholdning":0}"""
        private const val defaultInntektJson = """{"datoFom":"2001-01-01","inntekt":0.0}"""
        private const val defaultSimulerPensjonRequestJson = """{"fnr":"01010101010","sivilstandkode":"bogus","sprak":"json","simuleringsperioder":[$defaultSimuleringsperiodeJson],"simulertAFPOffentlig":null,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"pensjonsbeholdningsperioder":[$defaultPensjonsbeholdningsperiodeJson],"inntekter":[$defaultInntektJson]}"""
        private const val minimalSimulerPensjonRequestJson = """{"fnr":"01010101010","sivilstandkode":"bogus","simuleringsperioder":[],"inntekter":[]}"""
        private const val defaultSimuleringsdataJson = """{"poengArTom1991":0,"poengArFom1992":0,"sluttpoengtall":0.0,"anvendtTrygdetid":0,"basisgp":0.0,"basistp":0.0,"basispt":0.0,"forholdstall_uttak":0.0,"skjermingstillegg":0.0,"uforegradVedOmregning":0}"""
        private const val defaultSimulertAP2011Json = """{"simulertForsteuttak":$defaultSimuleringsdataJson,"simulertHeltUttakEtter67Ar":$defaultSimuleringsdataJson}"""
        private const val defaultStillingsprosentJson = """{"datoFom":"2001-01-01","datoTom":"2001-01-01","stillingsprosent":0.0,"aldersgrense":0,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"bogus"}"""
        private const val defaultTpForholdJson = """{"tpnr":"bogus","tssEksternId":"bogus","stillingsprosentListe":[$defaultStillingsprosentJson]}"""
        private const val defaultSimulerOffentligTjenestepensjonRequestJson = """{"fnr":"01010101010","tpnr":"bogus","tssEksternId":"bogus","forsteUttakDato":"2001-01-01","uttaksgrad":0,"heltUttakDato":"2001-01-01","stillingsprosentOffHeltUttak":0,"stillingsprosentOffGradertUttak":0,"inntektForUttak":0,"inntektUnderGradertUttak":0,"inntektEtterHeltUttak":0,"antallArInntektEtterHeltUttak":0,"sivilstandKode":"bogus","sprak":"json","simulertAFPOffentlig":0,"simulertAFPPrivat":$defaultSimulertAFPPrivatJson,"simulertAP2011":$defaultSimulertAP2011Json,"tpForholdListe":[$defaultTpForholdJson]}"""
    }
}