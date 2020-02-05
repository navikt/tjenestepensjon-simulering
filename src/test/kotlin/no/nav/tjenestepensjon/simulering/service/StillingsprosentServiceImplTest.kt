package no.nav.tjenestepensjon.simulering.service

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.Month
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class StillingsprosentServiceImplTest {
    @Mock
    var metrics: AppMetrics? = null
    @Mock
    var asyncExecutor: AsyncExecutor? = null
    @InjectMocks
    var stillingsprosentService: StillingsprosentServiceImpl? = null

    @Test
    fun shouldRetrieveFromTpRegisterAsync() {
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any(MutableMap::class.java))).thenReturn(Mockito.mock(AsyncResponse::class.java))
        stillingsprosentService.getStillingsprosentListe("123", Map.of(TPOrdning("1", "1"), TpLeverandor("name", "url", SOAP)))
        Mockito.verify<Any?>(asyncExecutor).executeAsync(ArgumentMatchers.any(MutableMap::class.java))
    }

    @Test
    fun handlesMetrics() {
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any(MutableMap::class.java))).thenReturn(Mockito.mock(AsyncResponse::class.java))
        stillingsprosentService.getStillingsprosentListe("123", Map.of(TPOrdning("1", "1"), TpLeverandor("name", "url", SOAP)))
        Mockito.verify<Any?>(metrics).incrementCounter(ArgumentMatchers.eq(APP_NAME), ArgumentMatchers.eq(APP_TOTAL_STILLINGSPROSENT_CALLS))
        Mockito.verify<Any?>(metrics).incrementCounter(ArgumentMatchers.eq(APP_NAME), ArgumentMatchers.eq(APP_TOTAL_STILLINGSPROSENT_TIME), ArgumentMatchers.any(Double::class.java))
    }

    @get:Throws(Exception::class)
    @get:Test
    val latestSingleForholdAndStillingsprosent: Unit
        get() {
            val map: MutableMap<TPOrdning, List<Stillingsprosent>> = HashMap<TPOrdning, List<Stillingsprosent>>()
            val tpOrdning = TPOrdning("1", "1")
            val pcts: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(createPct(jan2019, feb2019))
            map[tpOrdning] = pcts
            assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), `is`(tpOrdning))
        }

    @get:Throws(Exception::class)
    @get:Test
    val latestSingleForholdAndThreeStillingsprosent: Unit
        get() {
            val map: MutableMap<TPOrdning, List<Stillingsprosent>> = HashMap<TPOrdning, List<Stillingsprosent>>()
            val tpOrdning = TPOrdning("1", "1")
            val pcts: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                    createPct(jan2019, feb2019),
                    createPct(feb2019, apr2019),
                    createPct(jan2019, mar2019)
            )
            map[tpOrdning] = pcts
            assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), `is`(tpOrdning))
        }

    @get:Throws(Exception::class)
    @get:Test
    val latestSingleTwoForholdAndThreeStillingsprosent: Unit
        get() {
            val map: MutableMap<TPOrdning, List<Stillingsprosent>> = HashMap<TPOrdning, List<Stillingsprosent>>()
            val tpOrdning = TPOrdning("1", "1")
            val tpOrdning2 = TPOrdning("2", "2")
            val pcts1: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                    createPct(jan2019, feb2019),
                    createPct(feb2019, apr2019),
                    createPct(jan2019, mar2019)
            )
            map[tpOrdning] = pcts1
            val pcts2: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                    createPct(jan2019, feb2019),
                    createPct(feb2019, may2019)
            )
            map[tpOrdning2] = pcts2
            assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), `is`(tpOrdning2))
        }

    @Test
    fun throwsExceptionIfLatestEndDateIsNotUnique() {
        val map: MutableMap<TPOrdning, List<Stillingsprosent>> = HashMap<TPOrdning, List<Stillingsprosent>>()
        val tpOrdning = TPOrdning("1", "1")
        val tpOrdning2 = TPOrdning("2", "2")
        val pcts1: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                createPct(jan2019, may2019)
        )
        map[tpOrdning] = pcts1
        val pcts2: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                createPct(feb2019, may2019)
        )
        map[tpOrdning2] = pcts2
        Assertions.assertThrows(DuplicateStillingsprosentEndDateException::class.java, Executable { stillingsprosentService.getLatestFromStillingsprosent(map) })
    }

    @Test
    @Throws(Exception::class)
    fun nullIsGreaterThanDate() {
        val map: MutableMap<TPOrdning, List<Stillingsprosent>> = HashMap<TPOrdning, List<Stillingsprosent>>()
        val tpOrdning = TPOrdning("1", "1")
        val tpOrdning2 = TPOrdning("2", "2")
        val tpOrdning3 = TPOrdning("3", "3")
        val pcts1: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                createPct(jan2019, feb2019),
                createPct(feb2019, apr2019),
                createPct(apr2019, mar2019)
        )
        map[tpOrdning] = pcts1
        val pcts2: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                createPct(jan2019, feb2019),
                createPct(feb2019, may2019)
        )
        map[tpOrdning2] = pcts2
        val pcts3: List<Stillingsprosent> = java.util.List.of<Stillingsprosent>(
                createPct(jan2019, feb2019),
                createPct(feb2019, null)
        )
        map[tpOrdning3] = pcts3
        assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), `is`(tpOrdning3))
    }

    @Test
    fun throwsExceptionWhenNoStillingsprosentIsFound() {
        val map: MutableMap<TPOrdning, List<Stillingsprosent>> = HashMap<TPOrdning, List<Stillingsprosent>>()
        val tpOrdning = TPOrdning("1", "1")
        val tpOrdning2 = TPOrdning("2", "2")
        val pcts1: List<Stillingsprosent> = emptyList<Stillingsprosent>()
        map[tpOrdning] = pcts1
        val pcts2: List<Stillingsprosent> = emptyList<Stillingsprosent>()
        map[tpOrdning2] = pcts2
        Assertions.assertThrows(MissingStillingsprosentException::class.java, Executable { stillingsprosentService.getLatestFromStillingsprosent(map) })
    }

    private fun createPct(fom: LocalDate, tom: LocalDate?): Stillingsprosent {
        val s = Stillingsprosent()
        s.setDatoFom(fom)
        s.setDatoTom(tom)
        return s
    }

    companion object {
        private val jan2019 = LocalDate.of(2019, Month.JANUARY, 1)
        private val feb2019 = LocalDate.of(2019, Month.FEBRUARY, 1)
        private val mar2019 = LocalDate.of(2019, Month.MARCH, 1)
        private val apr2019 = LocalDate.of(2019, Month.APRIL, 1)
        private val may2019 = LocalDate.of(2019, Month.MAY, 1)
    }
}