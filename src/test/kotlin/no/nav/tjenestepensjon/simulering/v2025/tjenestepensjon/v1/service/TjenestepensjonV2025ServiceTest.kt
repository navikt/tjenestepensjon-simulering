package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningDto
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TomSimuleringFraTpOrdningException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPTjenestepensjonService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKTjenestepensjonService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

@SpringBootTest
class TjenestepensjonV2025ServiceTest {

    @MockitoBean
    private lateinit var tp: TpClient

    @MockitoBean
    private lateinit var spk: SPKTjenestepensjonService

    @MockitoBean
    private lateinit var klp: KLPTjenestepensjonService

    @Autowired
    private lateinit var tjenestepensjonV2025Service: TjenestepensjonV2025Service

    @Test
    fun `simuler success fra spk`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(listOf(dummyTpOrdning("3010")))
        `when`(spk.simuler(req,"3010")).thenReturn(dummyResult("spk", "3010"))

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isSuccess)
        val tjenestepensjon = res.second.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals("spk", tjenestepensjon.tpLeverandoer)
        assertEquals("3010", tjenestepensjon.tpNummer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals(1, tjenestepensjon.utbetalingsperioder.size)
    }

    @Test
    fun `simuler failure fra spk`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(listOf(dummyTpOrdning("3010")))
        `when`(spk.simuler(req,"3010")).thenReturn(Result.failure(WebClientResponseException("Failed to simulate", 500, "error", null, null, null)))

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isFailure)
        val tjenestepensjonFailure = res.second.exceptionOrNull()
        assertNotNull(tjenestepensjonFailure)
        assertEquals("Failed to simulate", tjenestepensjonFailure.message)
    }

    @Test
    fun `simuler naar tp-ordning stoettes ikke`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(listOf(dummyTpOrdning("9999")))

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isFailure)
        val tjenestepensjonFailure = res.second.exceptionOrNull()
        assertNotNull(tjenestepensjonFailure)
        assertTrue(tjenestepensjonFailure is TpOrdningStoettesIkkeException)
    }

    @Test
    fun `simuler tp naar bruker ikke er medlem i tp ordning`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(emptyList())

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isFailure)
        val tjenestepensjonFailure = res.second.exceptionOrNull()
        assertNotNull(tjenestepensjonFailure)
        assertTrue(tjenestepensjonFailure is BrukerErIkkeMedlemException)
    }

    @Test
    fun `simuler naar tpregisteret feilet`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenThrow(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))

        try {
            tjenestepensjonV2025Service.simuler(req)
            fail("Expected ResponseStatusException")
        } catch (
            e: ResponseStatusException
        ) {
            assertTrue(e.statusCode.is5xxServerError)
        }
    }

    @Test
    fun `simuler success fra klp 4082`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(listOf(dummyTpOrdning("4082")))
        `when`(klp.simuler(req, "4082")).thenReturn(dummyResult("klp", "4082"))

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isSuccess)
        val tjenestepensjon = res.second.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals("klp", tjenestepensjon.tpLeverandoer)
        assertEquals("4082", tjenestepensjon.tpNummer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals(1, tjenestepensjon.utbetalingsperioder.size)
    }

    @Test
    fun `simuler success fra klp 3200`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(listOf(dummyTpOrdning("3200")))
        `when`(klp.simuler(req, "3200")).thenReturn(dummyResult("klp", "3200"))

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isSuccess)
        val tjenestepensjon = res.second.getOrNull()
        assertNotNull(tjenestepensjon)
        assertEquals("klp", tjenestepensjon.tpLeverandoer)
        assertEquals("3200", tjenestepensjon.tpNummer)
        assertEquals(1, tjenestepensjon.ordningsListe.size)
        assertEquals(1, tjenestepensjon.utbetalingsperioder.size)
    }

    @Test
    fun `simulering feiler naar spk og klp returnerer tomt resultat`() {
        val req = dummyRequest("1963-02-05")
        `when`(tp.findTPForhold(req.pid)).thenReturn(listOf(dummyTpOrdning("3010"), dummyTpOrdning("4082")))
        `when`(spk.simuler(req, "3010")).thenReturn(Result.failure(TomSimuleringFraTpOrdningException("3010")))
        `when`(klp.simuler(req, "4082")).thenReturn(Result.failure(TomSimuleringFraTpOrdningException("4082")))

        val res: Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> = tjenestepensjonV2025Service.simuler(req)

        assertTrue(res.second.isFailure)
        val tjenestepensjonFailure = res.second.exceptionOrNull()
        assertNotNull(tjenestepensjonFailure)
        assertTrue(tjenestepensjonFailure is TomSimuleringFraTpOrdningException)
    }



    companion object {
        fun dummyRequest(foedselsdato: String, brukerBaOmAfp: Boolean = false) = SimulerTjenestepensjonRequestDto(
            "12345678910",
            LocalDate.parse(foedselsdato),
            LocalDate.parse("2025-03-01"),
            500000,
            0,
            brukerBaOmAfp,
            false,
            false
        )

        fun dummyTpOrdning(tpNummer: String) = TpOrdningDto("Statens pensjonskasse", tpNummer, "123456789", listOf("spk"))

        fun dummyResult(leverandoer: String, tpNummer: String) = Result.success(
            SimulertTjenestepensjonMedMaanedsUtbetalinger(
                leverandoer,
                tpNummer,
                listOf(Ordning("3010")),
                listOf(
                    Maanedsutbetaling(
                        LocalDate.parse("2025-03-01"),
                        Alder(61, 2),
                        5000
                    )
                )
            )
        )
    }
}
