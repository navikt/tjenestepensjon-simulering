package no.nav.tjenestepensjon.simulering.v1.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_TIME
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.v1.StillingsprosentCallable
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentCallableMap
import no.nav.tjenestepensjon.simulering.v1.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.v1.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class StillingsprosentServiceImplTest {
    @MockK(relaxed = true)
    lateinit var metrics: AppMetrics

    @MockK
    lateinit var asyncExecutor: AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable>

    @MockK
    lateinit var soapClient: SoapClient

    @InjectMockKs
    lateinit var stillingsprosentService: StillingsprosentServiceImpl

    private val fnr = FNR("01011234567")
    private val tpOrdning1 = TPOrdning("1", "1")
    private val tpOrdning2 = TPOrdning("2", "2")
    private val tpOrdning3 = TPOrdning("3", "3")
    private val jan2019 = LocalDate.of(2019, 1, 1)
    private val feb2019 = LocalDate.of(2019, 2, 1)
    private val mar2019 = LocalDate.of(2019, 3, 1)
    private val apr2019 = LocalDate.of(2019, 4, 1)
    private val may2019 = LocalDate.of(2019, 5, 1)

    @BeforeEach
    private fun mockMetrics() {
        every { metrics.startTime() } returns 0
        every { metrics.elapsedSince(any()) } returns 0
    }

    @Test
    fun `Should retrieve from tp register async`() {
        every { asyncExecutor.executeAsync(any<TPOrdningStillingsprosentCallableMap>()) } returns AsyncResponse()
        stillingsprosentService.getStillingsprosentListe(
            fnr, mapOf(TPOrdning("1", "1") to TpLeverandor("name", SOAP, "sim", "stilling"))
        )
        verify { asyncExecutor.executeAsync(any<TPOrdningStillingsprosentCallableMap>()) }
    }

    @Test
    fun `Handles metrics`() {
        every { asyncExecutor.executeAsync(any<TPOrdningStillingsprosentCallableMap>()) } returns AsyncResponse()
        stillingsprosentService.getStillingsprosentListe(
            fnr, mapOf(TPOrdning("1", "1") to TpLeverandor("name", SOAP, "sim", "stilling"))
        )
        verify { metrics.incrementCounter(eq(APP_NAME), eq(APP_TOTAL_OPPTJENINGSPERIODE_CALLS)) }
        verify { metrics.incrementCounter(eq(APP_NAME), eq(APP_TOTAL_OPPTJENINGSPERIODE_TIME), any()) }
    }

    @Test
    fun `Latest single forhold and stillingsprosent`() {
        val tpOrdning = TPOrdning("1", "1")
        val pcts = listOf(createPct(jan2019, feb2019))
        val map = mapOf(tpOrdning to pcts)
        assertEquals(tpOrdning, stillingsprosentService.getLatestFromStillingsprosent(map))
    }

    @Throws(Exception::class)
    @Test
    fun `Latest single forhold and three stillingsprosent`() {
        val tpOrdning = TPOrdning("1", "1")
        val pcts: List<Stillingsprosent> = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, apr2019), createPct(jan2019, mar2019)
        )
        val map = mapOf(tpOrdning to pcts)
        assertEquals(tpOrdning, stillingsprosentService.getLatestFromStillingsprosent(map))
    }

    @Throws(Exception::class)
    @Test
    fun `Latest single two forhold and three stillingsprosent`() {
        val pcts1 = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, apr2019), createPct(jan2019, mar2019)
        )
        val pcts2 = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, may2019)
        )
        val map = mapOf(
            tpOrdning1 to pcts1, tpOrdning2 to pcts2
        )
        assertEquals(tpOrdning2, stillingsprosentService.getLatestFromStillingsprosent(map))
    }

    @Test
    fun `Throws exception if latest end date is not unique`() {
        val pcts1 = listOf(
            createPct(jan2019, may2019)
        )
        val pcts2 = listOf(
            createPct(feb2019, may2019)
        )
        val map = mapOf(
            tpOrdning1 to pcts1, tpOrdning2 to pcts2
        )
        assertThrows<DuplicateStillingsprosentEndDateException> {
            stillingsprosentService.getLatestFromStillingsprosent(
                map
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun nullIsGreaterThanDate() {
        val pcts1 = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, apr2019), createPct(apr2019, mar2019)
        )
        val pcts2 = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, may2019)
        )
        val pcts3 = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, null)
        )

        val map = mapOf(
            tpOrdning1 to pcts1, tpOrdning2 to pcts2, tpOrdning3 to pcts3
        )
        assertEquals(tpOrdning3, stillingsprosentService.getLatestFromStillingsprosent(map))
    }

    @Test
    fun `Throws exception when no stillingsprosent is found`() {
        val pcts1: List<Stillingsprosent> = emptyList()
        val pcts2: List<Stillingsprosent> = emptyList()
        val map = mapOf(
            tpOrdning1 to pcts1, tpOrdning2 to pcts2
        )
        assertThrows<MissingStillingsprosentException> { stillingsprosentService.getLatestFromStillingsprosent(map) }
    }

    private fun createPct(fom: LocalDate, tom: LocalDate?) = Stillingsprosent(
        datoFom = fom,
        datoTom = tom,
        stillingsprosent = 0.0,
        aldersgrense = 0,
        faktiskHovedlonn = "",
        stillingsuavhengigTilleggslonn = "",
        utvidelse = null
    )
}
