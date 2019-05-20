package no.nav.tjenestepensjon.simulering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_LATEST_SIMULERING_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_LATEST_STILLINGSPROSENT_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_SIMULERING_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

class TjenestepensjonSimuleringMetricsTest {

    private static final TpLeverandor TP1 = new TpLeverandor("1", "url1");
    private static final TpLeverandor TP2 = new TpLeverandor("2", "url2");
    private static final List<TpLeverandor> tpLeverandorList = List.of(TP1, TP2);
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Test
    void incrementForApp() {
        TjenestepensjonSimuleringMetrics metrics = new TjenestepensjonSimuleringMetrics(meterRegistry, tpLeverandorList);
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS);
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME, 500);

        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS), is(1d));
        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME), is(500d));
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), is(0d));
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), is(0d));
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), is(0d));
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), is(0d));
    }

    @Test
    void incrementForTpOrdning() {
        TjenestepensjonSimuleringMetrics metrics = new TjenestepensjonSimuleringMetrics(meterRegistry, tpLeverandorList);
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS);
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 500d);
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_SIMULERING_CALLS);
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_SIMULERING_TIME, 111);

        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS), is(0d));
        assertThat(metrics.getCounterValue(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME), is(0d));
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), is(1d));
        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), is(0d));
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS), is(0d));
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), is(500d));
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_SIMULERING_CALLS), is(1d));
        assertThat(metrics.getCounterValue(TP2.getName(), TP_TOTAL_SIMULERING_TIME), is(111d));
    }

    @Test
    void gaugesLatestTime() {
        TjenestepensjonSimuleringMetrics metrics = new TjenestepensjonSimuleringMetrics(meterRegistry, tpLeverandorList);
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 500d);
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_SIMULERING_TIME, 111d);

        assertThat(metrics.getCounterValue(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME), is(500d));
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), is(500d));
        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_SIMULERING_TIME), is(111d));

        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 123d);

        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), is(123d));
    }

    @Test
    void gaugesLatestForEachTpOrdning() {
        TjenestepensjonSimuleringMetrics metrics = new TjenestepensjonSimuleringMetrics(meterRegistry, tpLeverandorList);
        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 500d);
        metrics.incrementCounter(TP2.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 100d);

        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), is(500d));
        assertThat(metrics.getGaugeValue(TP2.getName(), TP_LATEST_STILLINGSPROSENT_TIME), is(100d));

        metrics.incrementCounter(TP1.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, 123d);

        assertThat(metrics.getGaugeValue(TP1.getName(), TP_LATEST_STILLINGSPROSENT_TIME), is(123d));
        assertThat(metrics.getGaugeValue(TP2.getName(), TP_LATEST_STILLINGSPROSENT_TIME), is(100d));
    }
}