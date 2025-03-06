package no.nav.tjenestepensjon.simulering

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_LATEST_OPPTJENINGSPERIODE_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_LATEST_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_OPPTJENINGSPERIODE_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_OPPTJENINGSPERIODE_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.rest.SimuleringEndpoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AppMetricsTest {
    private val meterRegistry: SimpleMeterRegistry = SimpleMeterRegistry()


    private lateinit var metrics: AppMetrics

    @BeforeEach
    fun reset() {
        metrics = AppMetrics(meterRegistry)
    }

    @Test
    fun `Increment for app`() {
        metrics.incrementCounter(APP_NAME, APP_TOTAL_OPPTJENINGSPERIODE_CALLS)
        metrics.incrementCounter(APP_NAME, APP_TOTAL_OPPTJENINGSPERIODE_TIME, 500.0)
        assertEquals(1.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_OPPTJENINGSPERIODE_CALLS))
        assertEquals(500.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_OPPTJENINGSPERIODE_TIME))
        assertEquals(0.0, metrics.getCounterValue(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_CALLS))
        assertEquals(0.0, metrics.getCounterValue(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME))
    }

    @Test
    fun `Increment for tp ordning`() {
        metrics.incrementCounter(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_CALLS)
        assertEquals(0.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_OPPTJENINGSPERIODE_CALLS))
        assertEquals(0.0, metrics.getCounterValue(APP_NAME, APP_TOTAL_OPPTJENINGSPERIODE_TIME))
        assertEquals(1.0, metrics.getCounterValue(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_CALLS))
        assertEquals(0.0, metrics.getCounterValue(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME))
    }

    @Test
    fun `Gauges latest time`() {
        metrics.incrementCounter(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME, 500.0)
        metrics.incrementCounter(SimuleringEndpoint.PROVIDER, TP_TOTAL_SIMULERING_TIME, 111.0)
        assertEquals(500.0, metrics.getCounterValue(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME))
        assertEquals(500.0, metrics.getGaugeValue(SimuleringEndpoint.PROVIDER, TP_LATEST_OPPTJENINGSPERIODE_TIME))
        assertEquals(111.0, metrics.getGaugeValue(SimuleringEndpoint.PROVIDER, TP_LATEST_SIMULERING_TIME))
        metrics.incrementCounter(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME, 123.0)
        assertEquals(123.0, metrics.getGaugeValue(SimuleringEndpoint.PROVIDER, TP_LATEST_OPPTJENINGSPERIODE_TIME))
    }

    @Test
    fun `Gauges latest for each tp ordning`() {
        metrics.incrementCounter(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME, 500.0)
        assertEquals(500.0, metrics.getGaugeValue(SimuleringEndpoint.PROVIDER, TP_LATEST_OPPTJENINGSPERIODE_TIME))
        metrics.incrementCounter(SimuleringEndpoint.PROVIDER, TP_TOTAL_OPPTJENINGSPERIODE_TIME, 123.0)
        assertEquals(123.0, metrics.getGaugeValue(SimuleringEndpoint.PROVIDER, TP_LATEST_OPPTJENINGSPERIODE_TIME))
    }
}