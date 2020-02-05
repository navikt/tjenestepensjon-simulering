package no.nav.tjenestepensjon.simulering

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
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
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class AppMetricsTest {
    private val meterRegistry: MeterRegistry = SimpleMeterRegistry()
    @Test
    fun incrementForApp() {
        val metrics = AppMetrics(meterRegistry, tpLeverandorList)
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS)
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME, 500)
        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS), Matchers.`is`(1.0))
        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(500.0))
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(0.0))
    }

    @Test
    fun incrementForTpOrdning() {
        val metrics = AppMetrics(meterRegistry, tpLeverandorList)
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS)
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_SIMULERING_CALLS)
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_SIMULERING_TIME, 111)
        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), Matchers.`is`(1.0))
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), Matchers.`is`(0.0))
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(500.0))
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_SIMULERING_CALLS), Matchers.`is`(1.0))
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_SIMULERING_TIME), Matchers.`is`(111.0))
    }

    @Test
    fun gaugesLatestTime() {
        val metrics = AppMetrics(meterRegistry, tpLeverandorList)
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_SIMULERING_TIME, 111.0)
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), Matchers.`is`(500.0))
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), Matchers.`is`(500.0))
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_SIMULERING_TIME), Matchers.`is`(111.0))
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 123.0)
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), Matchers.`is`(123.0))
    }

    @Test
    fun gaugesLatestForEachTpOrdning() {
        val metrics = AppMetrics(meterRegistry, tpLeverandorList)
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 500.0)
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 100.0)
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), Matchers.`is`(500.0))
        assertThat(metrics.getGaugeValue(TP2.getName(), TP_LATEST_STILLINGSPROSENT_TIME), Matchers.`is`(100.0))
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 123.0)
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), Matchers.`is`(123.0))
        assertThat(metrics.getGaugeValue(TP2.getName(), TP_LATEST_STILLINGSPROSENT_TIME), Matchers.`is`(100.0))
    }

    companion object {
        private val TP1: TpLeverandor = TpLeverandor("1", "url1", SOAP)
        private val TP2: TpLeverandor = TpLeverandor("2", "url2", SOAP)
        private val tpLeverandorList: List<TpLeverandor> = java.util.List.of<TpLeverandor>(TP1, TP2)
    }
}