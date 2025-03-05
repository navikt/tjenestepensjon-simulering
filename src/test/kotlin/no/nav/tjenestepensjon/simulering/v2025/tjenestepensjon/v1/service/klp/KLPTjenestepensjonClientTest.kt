package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.SammenlignAFPService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025ServiceTest.Companion.dummyRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPMapper.PROVIDER_FULLT_NAVN
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPTjenestepensjonClient.Companion.SIMULER_PATH
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.InkludertOrdning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.KLPSimulerTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.Utbetaling
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.util.ReflectionTestUtils.setField
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KLPTjenestepensjonClientTest {

    @MockitoBean
    private lateinit var sammenlignAFPService: SammenlignAFPService

    @MockitoBean
    private lateinit var aadClient: AADClient

    @MockitoBean
    private lateinit var maskinportenTokenClient: MaskinportenTokenClient

    @Autowired
    private lateinit var klpClient: KLPTjenestepensjonClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private var wireMockServer = WireMockServer().apply {
        start()
    }

    @BeforeAll
    fun beforeAll() {
        Mockito.`when`(maskinportenTokenClient.pensjonsimuleringToken(anyNonNull())).thenReturn("bogustoken")
        Mockito.doNothing().`when`(sammenlignAFPService).sammenlignOgLoggAfp(anyNonNull(), anyNonNull())
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `send request og les respons med tjenestepensjon fra klp`() {
        setField(klpClient, "activeProfiles", "prod-gcp")
        val tpNummer = "995566"
        val mockResponse = klpSimulerTjenestepensjonResponse()
        val stub = wireMockServer.stubFor(post(urlPathEqualTo("$SIMULER_PATH/$tpNummer")).willReturn(okJson(objectMapper.writeValueAsString(mockResponse))))

        val response: Result<SimulertTjenestepensjon> = klpClient.simuler(dummyRequest("1963-02-05", brukerBaOmAfp = true), tpNummer)

        assertTrue(response.isSuccess)
        val tjenestepensjon = response.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals(PROVIDER_FULLT_NAVN, tjenestepensjon!!.tpLeverandoer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals(tpNummer, tjenestepensjon.ordningsListe[0].tpNummer)
        assertEquals(4, tjenestepensjon.utbetalingsperioder.size)
        assertEquals(mockResponse.utbetalingsListe[0].fraOgMedDato, tjenestepensjon.utbetalingsperioder[0].fom)
        assertEquals(mockResponse.utbetalingsListe[0].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[0].maanedligBelop)
        assertEquals(mockResponse.utbetalingsListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[0].ytelseType)
        assertEquals(mockResponse.utbetalingsListe[1].fraOgMedDato, tjenestepensjon.utbetalingsperioder[1].fom)
        assertEquals(mockResponse.utbetalingsListe[1].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[1].maanedligBelop)
        assertEquals(mockResponse.utbetalingsListe[1].ytelseType, tjenestepensjon.utbetalingsperioder[1].ytelseType)
        assertEquals(mockResponse.utbetalingsListe[2].fraOgMedDato, tjenestepensjon.utbetalingsperioder[2].fom)
        assertEquals(mockResponse.utbetalingsListe[2].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[2].maanedligBelop)
        assertEquals(mockResponse.utbetalingsListe[2].ytelseType, tjenestepensjon.utbetalingsperioder[2].ytelseType)
        assertEquals(mockResponse.utbetalingsListe[3].fraOgMedDato, tjenestepensjon.utbetalingsperioder[3].fom)
        assertEquals(mockResponse.utbetalingsListe[3].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[3].maanedligBelop)
        assertEquals(mockResponse.utbetalingsListe[3].ytelseType, tjenestepensjon.utbetalingsperioder[3].ytelseType)
        assertEquals(3, tjenestepensjon.aarsakIngenUtbetaling.size)
        assertTrue(tjenestepensjon.aarsakIngenUtbetaling.containsAll(mockResponse.arsakIngenUtbetaling))

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `send request og faa error fra klp`() {
        val stub = wireMockServer.stubFor(post(urlPathEqualTo(SIMULER_PATH)).willReturn(serverError()))

        val response: Result<SimulertTjenestepensjon> = klpClient.simuler(dummyRequest("1963-02-05", brukerBaOmAfp = true), "3100")
        assertTrue(response.isFailure)
        assertTrue(response.exceptionOrNull() is TjenestepensjonSimuleringException)

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `ikke send request og returner mock i dev-gcp fra klp`() {
        setField(klpClient, "activeProfiles", "dev-gcp")
        val request = dummyRequest("1963-02-05", brukerBaOmAfp = true)
        val tpNummer = "3100"

        val mockExpectedResponse = KLPTjenestepensjonClient.provideMockResponse(request)

        val stub = wireMockServer.stubFor(post(urlPathEqualTo("$SIMULER_PATH/$tpNummer")).willReturn(serverError()))

        val response: Result<SimulertTjenestepensjon> = klpClient.simuler(request, tpNummer)

        assertTrue(response.isSuccess)
        val tjenestepensjon = response.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals(PROVIDER_FULLT_NAVN, tjenestepensjon!!.tpLeverandoer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals(tpNummer, tjenestepensjon.ordningsListe[0].tpNummer)
        assertEquals(3, tjenestepensjon.utbetalingsperioder.size)

        assertEquals(mockExpectedResponse.utbetalingsListe[0].fraOgMedDato, tjenestepensjon.utbetalingsperioder[0].fom)
        assertEquals(mockExpectedResponse.utbetalingsListe[0].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[0].maanedligBelop)
        assertEquals(mockExpectedResponse.utbetalingsListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[0].ytelseType)
        assertEquals(mockExpectedResponse.utbetalingsListe[1].fraOgMedDato, tjenestepensjon.utbetalingsperioder[1].fom)
        assertEquals(mockExpectedResponse.utbetalingsListe[1].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[1].maanedligBelop)
        assertEquals(mockExpectedResponse.utbetalingsListe[1].ytelseType, tjenestepensjon.utbetalingsperioder[1].ytelseType)
        assertEquals(mockExpectedResponse.utbetalingsListe[2].fraOgMedDato, tjenestepensjon.utbetalingsperioder[2].fom)
        assertEquals(mockExpectedResponse.utbetalingsListe[2].manedligUtbetaling, tjenestepensjon.utbetalingsperioder[2].maanedligBelop)
        assertEquals(mockExpectedResponse.utbetalingsListe[2].ytelseType, tjenestepensjon.utbetalingsperioder[2].ytelseType)
        assertEquals(0, tjenestepensjon.aarsakIngenUtbetaling.size)

        wireMockServer.removeStub(stub.uuid)
    }

    private fun klpSimulerTjenestepensjonResponse() = KLPSimulerTjenestepensjonResponse(
        inkludertOrdningListe = listOf(InkludertOrdning("995566")),
        utbetalingsListe = listOf(
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                manedligUtbetaling = 1,
                arligUtbetaling = 12,
                ytelseType = "OAFP",
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                manedligUtbetaling = 2,
                arligUtbetaling = 24,
                ytelseType = "PAASLAG",
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                manedligUtbetaling = 3,
                arligUtbetaling = 36,
                ytelseType = "APOF2020",
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                manedligUtbetaling = 4,
                arligUtbetaling = 48,
                ytelseType = "OT6370",
            )
        ),
        arsakIngenUtbetaling = listOf("IKKE_STOETTET", "Ikke stoettet", "SAERALDERSPAASLAG"),
        betingetTjenestepensjonErInkludert = false,
    )

}