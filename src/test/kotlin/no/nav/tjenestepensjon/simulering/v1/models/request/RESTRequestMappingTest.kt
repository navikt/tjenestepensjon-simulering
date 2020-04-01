package no.nav.tjenestepensjon.simulering.v1.models.request

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v1.models.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

        val test = """{"tpnr":null,"fnr":"01015438580","sivilstandkode":"GIFT","sprak":null,"simuleringsperioder":[{"datoFom":"2026-06-30","utg":100,"stillingsprosentOffentlig":null,"poengArTom1991":17,"poengArFom1992":22,"sluttpoengtall":3.26,"anvendtTrygdetid":40,"forholdstall":0.769,"delingstall":null,"uforegradVedOmregning":null,"delytelser":[{"pensjonstype":"basisgp","belop":99858.0},{"pensjonstype":"basistp","belop":137458.03203},{"pensjonstype":"basispt","belop":-35182.020621469186},{"pensjonstype":"inntektspensjon","belop":21127.0},{"pensjonstype":"garantipensjon","belop":9261.0},{"pensjonstype":"garantitillegg","belop":-90017.0}]}],"simulertAFPOffentlig":null,"simulertAFPPrivat":null,"tpForhold":null,"pensjonsbeholdningperioder":[{"pensjonsbeholdning":2305038.088209813,"garantipensjonsbeholdning":1010392.4323326237,"garantitilleggsbeholdning":-982091.7088591022,"datoFom":"2026-06-30"}],"inntekter":[{"datoFom":"2019-01-01","inntekt":500000.0},{"datoFom":"2026-06-30","inntekt":0.0},{"datoFom":"2026-06-30","inntekt":0.0}]}"""

        val result = objectMapper.readValue(defaultSimulerPensjonRequestJson, SimulerPensjonRequest::class.java)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonRequestJson, SimulerOffentligTjenestepensjonRequest::class.java)
        assertEquals(objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonRequest), objectMapper.writeValueAsString(result))
    }
}