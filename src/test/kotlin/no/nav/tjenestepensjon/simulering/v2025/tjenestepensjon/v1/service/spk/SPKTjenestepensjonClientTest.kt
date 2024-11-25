package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.service.AADClient
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025ServiceTest.Companion.dummyRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKMapper.PROVIDER_FULLT_NAVN
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKTjenestepensjonClient.Companion.SIMULER_PATH
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SPKTjenestepensjonClientTest{

    @MockBean
    private lateinit var aadClient: AADClient
    @MockBean
    private lateinit var maskinportenTokenClient: MaskinportenTokenClient
    @Autowired
    private lateinit var spkClient: SPKTjenestepensjonClient
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private var wireMockServer = WireMockServer().apply {
        start()
    }

    @BeforeAll
    fun beforeAll() {
        Mockito.`when`(maskinportenTokenClient.pensjonsimuleringToken(anyNonNull())).thenReturn("bogustoken")
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `send request og les respons med tjenestepensjon fra spk`() {
        val mockResponse = spkSimulerTjenestepensjonResponse()
        val stub = wireMockServer.stubFor(post(urlPathEqualTo(SIMULER_PATH)).willReturn(okJson(objectMapper.writeValueAsString(mockResponse))))

        val response: Result<SimulertTjenestepensjon> = spkClient.simuler(dummyRequest("1963-02-05", brukerBaOmAfp = true))

        assertTrue(response.isSuccess)
        val tjenestepensjon = response.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals(PROVIDER_FULLT_NAVN, tjenestepensjon!!.tpLeverandoer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals("3010", tjenestepensjon.ordningsListe[0].tpNummer)
        assertEquals(5, tjenestepensjon.utbetalingsperioder.size)
        assertEquals(mockResponse.utbetalingListe[0].fraOgMedDato, tjenestepensjon.utbetalingsperioder[0].fom)
        assertEquals(mockResponse.utbetalingListe[0].delytelseListe[0].maanedligBelop, tjenestepensjon.utbetalingsperioder[0].maanedligBelop)
        assertEquals(mockResponse.utbetalingListe[0].delytelseListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[0].ytelseType)
        assertEquals(mockResponse.utbetalingListe[1].fraOgMedDato, tjenestepensjon.utbetalingsperioder[1].fom)
        assertEquals(mockResponse.utbetalingListe[1].delytelseListe[0].maanedligBelop, tjenestepensjon.utbetalingsperioder[1].maanedligBelop)
        assertEquals(mockResponse.utbetalingListe[1].delytelseListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[1].ytelseType)
        assertEquals(mockResponse.utbetalingListe[2].fraOgMedDato, tjenestepensjon.utbetalingsperioder[2].fom)
        assertEquals(mockResponse.utbetalingListe[2].delytelseListe[0].maanedligBelop, tjenestepensjon.utbetalingsperioder[2].maanedligBelop)
        assertEquals(mockResponse.utbetalingListe[2].delytelseListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[2].ytelseType)
        assertEquals(mockResponse.utbetalingListe[3].fraOgMedDato, tjenestepensjon.utbetalingsperioder[3].fom)
        assertEquals(mockResponse.utbetalingListe[3].delytelseListe[0].maanedligBelop, tjenestepensjon.utbetalingsperioder[3].maanedligBelop)
        assertEquals(mockResponse.utbetalingListe[3].delytelseListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[3].ytelseType)
        assertEquals(mockResponse.utbetalingListe[4].fraOgMedDato, tjenestepensjon.utbetalingsperioder[4].fom)
        assertEquals(mockResponse.utbetalingListe[4].delytelseListe[0].maanedligBelop, tjenestepensjon.utbetalingsperioder[4].maanedligBelop)
        assertEquals(mockResponse.utbetalingListe[4].delytelseListe[0].ytelseType, tjenestepensjon.utbetalingsperioder[4].ytelseType)
        assertEquals(1, tjenestepensjon.aarsakIngenUtbetaling.size)
        assertTrue(tjenestepensjon.aarsakIngenUtbetaling[0].contains(mockResponse.aarsakIngenUtbetaling[0].ytelseType))

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `send request og faa error fra spk`() {
        val stub = wireMockServer.stubFor(post(urlPathEqualTo(SIMULER_PATH)).willReturn(serverError()))

        val response: Result<SimulertTjenestepensjon> = spkClient.simuler(dummyRequest("1963-02-05", brukerBaOmAfp = true))
        assertTrue(response.isFailure)
        assertTrue(response.exceptionOrNull() is TjenestepensjonSimuleringException)

        wireMockServer.removeStub(stub.uuid)
    }

    private fun spkSimulerTjenestepensjonResponse() = SPKSimulerTjenestepensjonResponse(
        inkludertOrdningListe = listOf(InkludertOrdning("3010")),
        utbetalingListe = listOf(
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                delytelseListe = listOf(Delytelse("OAFP", 1)),
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                delytelseListe = listOf(Delytelse("PAASLAG", 2)),
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                delytelseListe = listOf(Delytelse("APOF2020", 3)),
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                delytelseListe = listOf(Delytelse("OT6370", 4)),
            ),
            Utbetaling(
                fraOgMedDato = LocalDate.parse("2025-03-01"),
                delytelseListe = listOf(Delytelse("AFP", 5)),
            )
        ),
        aarsakIngenUtbetaling = listOf(
            AarsakIngenUtbetaling("IKKE_STOETTET", "Ikke stoettet", "SAERALDERSPAASLAG"),
        )
    )


}