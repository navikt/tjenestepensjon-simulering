package no.nav.tjenestepensjon.simulering.model.v1.response

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.Utbetalingsperiode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.util.Collections.singletonList
import kotlin.test.assertEquals

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class])
class RESTResponseMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test serializing of SimulertPensjon OK`() {
        val result = objectMapper.writeValueAsString(defaultSimulertPensjonOK)
        assertEquals(defaultSimulertPensjonOKJson, result)
    }

    @Test
    fun `Test serializing SimulertPensjon Feil`() {
        val result = objectMapper.writeValueAsString(defaultSimulertPensjonFeil)
        assertEquals(defaultSimulertPensjonFeilJson, result)
    }

    @Test
    fun `Test serializing SimulerOffentligTjenestepensjonResponse`() {
        val result = objectMapper.writeValueAsString(defaultSimulerOffentligTjenestepensjonResponse)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponseJson, result)
    }

    @Test
    fun `Test serializing HentStillingsprosentListeResponse`() {
        val result = objectMapper.writeValueAsString(defaultHentStillingsprosentListeResponse)
        assertEquals(defaultHentStillingsprosentListeResponseJson, result)
    }

    @Test
    fun `Test deserializing of SimulertPensjon OK`() {
        val result = objectMapper.readValue(defaultSimulertPensjonOKJson, SimulertPensjon::class.java)
        assertEquals(defaultSimulertPensjonOK, result)
    }

    @Test
    fun `Test deserializing SimulertPensjon Feil`() {
        val result = objectMapper.readValue(defaultSimulertPensjonFeilJson, SimulertPensjon::class.java)
        assertEquals(defaultSimulertPensjonFeil, result)
    }

    @Test
    fun `Test deserializing SimulerOffentligTjenestepensjonResponse`() {
        val result = objectMapper.readValue(defaultSimulerOffentligTjenestepensjonResponseJson, SimulerOffentligTjenestepensjonResponse::class.java)
        assertEquals(defaultSimulerOffentligTjenestepensjonResponse, result)
    }

    @Test
    fun `Test deserializing HentStillingsprosentListeResponse`() {
        val result = objectMapper.readValue(defaultHentStillingsprosentListeResponseJson, HentStillingsprosentListeResponse::class.java)
        assertEquals(defaultHentStillingsprosentListeResponse, result)
    }

    companion object {
        private val defaultSimulertPensjonOK = SimulertPensjon(
                tpnr = "tpnr",
                navnOrdning = "navnOrdning",
                inkluderteOrdninger = listOf("inkluderteOrdninger a", "inkluderteOrdninger b"),
                leverandorUrl = "leverandorUrl",
                inkluderteTpnr = listOf("inkluderteTpnr a", "inkluderteTpnr b"),
                utelatteTpnr = listOf("utelatteTpnr a", "utelatteTpnr b"),
                utbetalingsperioder = listOf(
                        Utbetalingsperiode(
                                grad = 1,
                                arligUtbetaling = 1.0,
                                datoFom = LocalDate.of(2000, 1, 1),
                                datoTom = LocalDate.of(2001, 2, 2),
                                ytelsekode = "ytelsekode",
                                mangelfullSimuleringkode = "mangelfullSimuleringkode"
                        ),
                        null
                )
        )

        private val defaultSimulertPensjonFeil = SimulertPensjon(
                status = "test a",
                feilkode = "test b",
                feilbeskrivelse = "test c"
        )

        private val defaultHentStillingsprosentListeResponse = HentStillingsprosentListeResponse(
                singletonList(
                        Stillingsprosent(
                                aldersgrense = 1,
                                datoFom = LocalDate.of(2000, 1, 1),
                                datoTom = LocalDate.of(2001, 2, 2),
                                stillingsprosent = 0.0,
                                stillingsuavhengigTilleggslonn = "0",
                                faktiskHovedlonn = "bogus"
                        )
                )
        )

        private val defaultSimulerOffentligTjenestepensjonResponse = SimulerOffentligTjenestepensjonResponse(singletonList(defaultSimulertPensjonOK))

        private const val defaultSimulertPensjonOKJson = """{"tpnr":"tpnr","navnOrdning":"navnOrdning","inkluderteOrdninger":["inkluderteOrdninger a","inkluderteOrdninger b"],"leverandorUrl":"leverandorUrl","inkluderteTpnr":["inkluderteTpnr a","inkluderteTpnr b"],"utelatteTpnr":["utelatteTpnr a","utelatteTpnr b"],"utbetalingsperioder":[{"grad":1,"arligUtbetaling":1.0,"datoFom":"2000-01-01","datoTom":"2001-02-02","ytelsekode":"ytelsekode","mangelfullSimuleringkode":"mangelfullSimuleringkode"},null],"status":null,"feilkode":null,"feilbeskrivelse":null}"""
        private const val defaultSimulertPensjonFeilJson = """{"tpnr":null,"navnOrdning":null,"inkluderteOrdninger":null,"leverandorUrl":null,"inkluderteTpnr":null,"utelatteTpnr":null,"utbetalingsperioder":null,"status":"test a","feilkode":"test b","feilbeskrivelse":"test c"}"""
        private const val defaultSimulerOffentligTjenestepensjonResponseJson = """{"simulertPensjonListe":[$defaultSimulertPensjonOKJson]}"""
        private const val defaultHentStillingsprosentListeResponseJson = """[{"datoFom":"2000-01-01","datoTom":"2001-02-02","stillingsprosent":0.0,"aldersgrense":1,"faktiskHovedlonn":"bogus","stillingsuavhengigTilleggslonn":"0"}]"""
    }
}