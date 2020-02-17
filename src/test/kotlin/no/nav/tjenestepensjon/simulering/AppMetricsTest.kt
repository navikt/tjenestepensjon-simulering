package no.nav.tjenestepensjon.simulering

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_LATEST_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_LATEST_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach

internal class AppMetricsTest {
    private val meterRegistry: SimpleMeterRegistry = SimpleMeterRegistry()


    private val tp1: TpLeverandor = TpLeverandor("1", "url1", SOAP)
    private val tp2: TpLeverandor = TpLeverandor("2", "url2", SOAP)
    private val tpLeverandorList = listOf(tp1, tp2)

    private lateinit var metrics: AppMetrics

    @BeforeEach
    fun reset() {
        metrics = AppMetrics(meterRegistry, tpLeverandorList)
    }

    @Test
    fun `Increment for app`() {
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS)
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        assertEquals(1.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS))
        assertEquals(500.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME))
        assertEquals(0.0, metrics.getCounterValue(tp1.name, TP_TOTAL_STILLINGSPROSENT_CALLS))
        assertEquals(0.0, metrics.getCounterValue(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME))
        assertEquals(0.0, metrics.getCounterValue(tp2.name, TP_TOTAL_STILLINGSPROSENT_CALLS))
        assertEquals(0.0, metrics.getCounterValue(tp2.name, TP_TOTAL_STILLINGSPROSENT_TIME))
    }

    @Test
    fun `Increment for tp ordning`() {
        metrics.incrementCounter(tp1.name, TP_TOTAL_STILLINGSPROSENT_CALLS)
        metrics.incrementCounter(tp2.name, TP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        metrics.incrementCounter(tp2.name, TP_TOTAL_SIMULERING_CALLS)
        metrics.incrementCounter(tp2.name, TP_TOTAL_SIMULERING_TIME, 111.0)
        assertEquals(0.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS))
        assertEquals(0.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME))
        assertEquals(1.0, metrics.getCounterValue(tp1.name, TP_TOTAL_STILLINGSPROSENT_CALLS))
        assertEquals(0.0, metrics.getCounterValue(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME))
        assertEquals(0.0, metrics.getCounterValue(tp2.name, TP_TOTAL_STILLINGSPROSENT_CALLS))
        assertEquals(500.0, metrics.getCounterValue(tp2.name, TP_TOTAL_STILLINGSPROSENT_TIME))
        assertEquals(1.0, metrics.getCounterValue(tp2.name, TP_TOTAL_SIMULERING_CALLS))
        assertEquals(111.0, metrics.getCounterValue(tp2.name, TP_TOTAL_SIMULERING_TIME))
    }

    @Test
    fun `Gauges latest time`() {
        metrics.incrementCounter(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        metrics.incrementCounter(tp1.name, TP_TOTAL_SIMULERING_TIME, 111.0)
        assertEquals(500.0, metrics.getCounterValue(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME))
        assertEquals(500.0, metrics.getGaugeValue(tp1.name, TP_LATEST_STILLINGSPROSENT_TIME))
        assertEquals(111.0, metrics.getGaugeValue(tp1.name, TP_LATEST_SIMULERING_TIME))
        metrics.incrementCounter(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME, 123.0)
        assertEquals(123.0, metrics.getGaugeValue(tp1.name, TP_LATEST_STILLINGSPROSENT_TIME))
    }

    @Test
    fun `Gauges latest for each tp ordning`() {
        metrics.incrementCounter(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        metrics.incrementCounter(tp2.name, TP_TOTAL_STILLINGSPROSENT_TIME, 100.0)
        assertEquals(500.0, metrics.getGaugeValue(tp1.name, TP_LATEST_STILLINGSPROSENT_TIME))
        assertEquals(100.0, metrics.getGaugeValue(tp2.name, TP_LATEST_STILLINGSPROSENT_TIME))
        metrics.incrementCounter(tp1.name, TP_TOTAL_STILLINGSPROSENT_TIME, 123.0)
        assertEquals(123.0, metrics.getGaugeValue(tp1.name, TP_LATEST_STILLINGSPROSENT_TIME))
        assertEquals(100.0, metrics.getGaugeValue(tp2.name, TP_LATEST_STILLINGSPROSENT_TIME))
    }
}