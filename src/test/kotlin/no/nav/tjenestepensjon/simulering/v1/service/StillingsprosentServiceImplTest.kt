package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_TIME
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.testHelper.safeEq
import no.nav.tjenestepensjon.simulering.v1.StillingsprosentCallable
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentCallableMap
import no.nav.tjenestepensjon.simulering.v1.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.v1.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class StillingsprosentServiceImplTest {
    @Mock
    lateinit var metrics: AppMetrics

    @Mock
    lateinit var asyncExecutor: AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable>

    @Mock
    lateinit var soapClient: SoapClient

    @InjectMocks
    lateinit var stillingsprosentService: StillingsprosentServiceImpl

    private val fnr = FNR("01011234567")
    private val tpOrdningIdDto1 = TPOrdningIdDto("1", "1")
    private val tpOrdningIdDto2 = TPOrdningIdDto("2", "2")
    private val tpOrdningIdDto3 = TPOrdningIdDto("3", "3")
    private val jan2019 = LocalDate.of(2019, 1, 1)
    private val feb2019 = LocalDate.of(2019, 2, 1)
    private val mar2019 = LocalDate.of(2019, 3, 1)
    private val apr2019 = LocalDate.of(2019, 4, 1)
    private val may2019 = LocalDate.of(2019, 5, 1)

    @Test
    fun `Should retrieve from tp register async`() {
        `when`(asyncExecutor.executeAsync(anyNonNull<TPOrdningStillingsprosentCallableMap>())).thenReturn(AsyncResponse())
        stillingsprosentService.getStillingsprosentListe(
            fnr, mapOf(TPOrdningIdDto("1", "1") to TpLeverandor("name", SOAP, "sim", "stilling"))
        )
        verify(asyncExecutor).executeAsync(anyNonNull<TPOrdningStillingsprosentCallableMap>())
    }

    @Test
    fun `Handles metrics`() {
        `when`(asyncExecutor.executeAsync(anyNonNull<TPOrdningStillingsprosentCallableMap>())).thenReturn(AsyncResponse())
        stillingsprosentService.getStillingsprosentListe(
            fnr, mapOf(TPOrdningIdDto("1", "1") to TpLeverandor("name", SOAP, "sim", "stilling"))
        )
        verify(metrics).incrementCounter(safeEq(APP_NAME), safeEq(APP_TOTAL_OPPTJENINGSPERIODE_CALLS))
        verify(metrics).incrementCounter(safeEq(APP_NAME), safeEq(APP_TOTAL_OPPTJENINGSPERIODE_TIME), anyNonNull())
    }

    @Throws(Exception::class)
    @Test
    fun `Latest single forhold and stillingsprosent`() {
        val tpOrdningIdDto = TPOrdningIdDto("1", "1")
        val pcts = listOf(createPct(jan2019, feb2019))
        val map = mapOf(tpOrdningIdDto to pcts)
        assertEquals(tpOrdningIdDto, stillingsprosentService.getLatestFromStillingsprosent(map))
    }

    @Throws(Exception::class)
    @Test
    fun `Latest single forhold and three stillingsprosent`() {
        val tpOrdningIdDto = TPOrdningIdDto("1", "1")
        val pcts: List<Stillingsprosent> = listOf(
            createPct(jan2019, feb2019), createPct(feb2019, apr2019), createPct(jan2019, mar2019)
        )
        val map = mapOf(tpOrdningIdDto to pcts)
        assertEquals(tpOrdningIdDto, stillingsprosentService.getLatestFromStillingsprosent(map))
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
            tpOrdningIdDto1 to pcts1, tpOrdningIdDto2 to pcts2
        )
        assertEquals(tpOrdningIdDto2, stillingsprosentService.getLatestFromStillingsprosent(map))
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
            tpOrdningIdDto1 to pcts1, tpOrdningIdDto2 to pcts2
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
            tpOrdningIdDto1 to pcts1, tpOrdningIdDto2 to pcts2, tpOrdningIdDto3 to pcts3
        )
        assertEquals(tpOrdningIdDto3, stillingsprosentService.getLatestFromStillingsprosent(map))
    }

    @Test
    fun `Throws exception when no stillingsprosent is found`() {
        val pcts1: List<Stillingsprosent> = emptyList()
        val pcts2: List<Stillingsprosent> = emptyList()
        val map = mapOf(
            tpOrdningIdDto1 to pcts1, tpOrdningIdDto2 to pcts2
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
