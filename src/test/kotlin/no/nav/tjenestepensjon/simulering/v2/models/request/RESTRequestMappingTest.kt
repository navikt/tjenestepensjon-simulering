package no.nav.tjenestepensjon.simulering.v2.models.request

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.v2.models.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTRequestMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test serializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.writeValueAsString(defaultSimulertPensjonRequest)
        assertEquals(defaultSimulerOffentligTjenestepensjonRequestJson, result)
    }

    @Test
    fun `Test deserializing of SimulerOffentligTjenestepensjonRequest`() {
        val result = objectMapper.readValue(
                " {\"fnr\":\"03065947853\",\"fodselsdato\":\"1959-06-03\",\"sisteTpnr\":\"TPNR\",\"sprak\":\"nob\",\"simulertAFPOffentlig\":null,\"simulertAFPPrivat\":null,\"sivilstandkode\":\"GIFT\",\"inntektListe\":[{\"datoFom\":\"2019-01-01\",\"inntekt\":500000.0},{\"datoFom\":\"2026-07-01\",\"inntekt\":0.0},{\"datoFom\":\"2026-07-01\",\"inntekt\":0.0}],\"pensjonsbeholdningsperiodeListe\":[{\"datoFom\":\"2026-07-01\",\"pensjonsbeholdning\":2682684.0383089776,\"garantipensjonsbeholdning\":712104.4375602789,\"garantitilleggsbeholdning\":-1446597.2466624915}],\"simuleringsperiodeListe\":[{\"datoFom\":\"2026-07-01\",\"folketrygdUttaksgrad\":100,\"stillingsprosentOffentlig\":null,\"simulerAFPOffentligEtterfulgtAvAlderListe\":false}],\"simuleringsdataListe\":[{\"datoFom\":\"2026-07-01\",\"andvendtTrygdetid\":40,\"poengArTom1991\":5,\"poengArFom1992\":29,\"uforegradVedOmregning\":null,\"basisgp\":99858.0,\"basispt\":-11450.495320000016,\"basistp\":112394.17332,\"delingstallUttak\":15.73,\"forholdstallUttak\":1.099,\"sluttpoengtall\":3.12}],\"tpForholdListe\":null}",
                SimulerPensjonRequest::class.java)
         assertEquals(objectMapper.writeValueAsString(defaultSimulertPensjonRequest), objectMapper.writeValueAsString(result))
    }
}