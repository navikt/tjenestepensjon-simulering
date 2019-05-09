package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_LATEST_STILLINGSPROSENT_TIME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Component
public class TjenestepensjonSimuleringMetrics {

    private final MeterRegistry meterRegistry;
    private Map<String, Map<String, Meter>> metrics = new HashMap<>();
    private Map<String, Map<String, Number>> gaugeValues = new HashMap<>();

    public TjenestepensjonSimuleringMetrics(List<TPOrdning> tpOrdningList, MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initAppMetrics();
        initTpOrdningMetrics(tpOrdningList);
    }

    private void initAppMetrics() {
        Map<String, Meter> appMetrics = new HashMap<>();
        appMetrics.put(APP_TOTAL_STILLINGSPROSENT_CALLS, createCounter(APP_TOTAL_STILLINGSPROSENT_CALLS, "Totalt antall kall mot stillingsprosent"));
        appMetrics.put(APP_TOTAL_STILLINGSPROSENT_TIME, createCounter(APP_TOTAL_STILLINGSPROSENT_TIME, "Akkumulert responstid for alle kall mot stillingsprosent"));
        metrics.put(APP_NAME, appMetrics);
    }

    private void initTpOrdningMetrics(List<TPOrdning> tpOrdningList) {
        tpOrdningList.forEach(tpOrdning -> metrics.put(tpOrdning.getTpId(), metricsFor(tpOrdning)));
    }

    private Map<String, Meter> metricsFor(TPOrdning tpOrdning) {
        Map<String, Meter> tpMetrics = new HashMap<>();
        tpMetrics.put(TP_TOTAL_STILLINGSPROSENT_CALLS,
                createCounter(TP_TOTAL_STILLINGSPROSENT_CALLS + tpOrdning.getTpId(), "Totalt antall kall mot stillingsprosent for aktuell tp-ordning"));
        tpMetrics.put(TP_TOTAL_STILLINGSPROSENT_TIME,
                createCounter(TP_TOTAL_STILLINGSPROSENT_TIME + tpOrdning.getTpId(), "Akkumulert responstid for aktuell tp-ordning"));
        tpMetrics.put(TP_LATEST_STILLINGSPROSENT_TIME,
                createGauge(tpOrdning.getTpId(), TP_LATEST_STILLINGSPROSENT_TIME));
        return tpMetrics;
    }

    private Map<String, Number> gaugeValueFor(String metric) {
        Map<String, Number> values = new HashMap<>();
        values.put(metric, 0);
        return values;
    }

    private Counter createCounter(String metric, String description) {
        return Counter.builder(metric).description(description).register(meterRegistry);
    }

    private Gauge createGauge(String prefix, String metric) {
        gaugeValues.put(prefix, gaugeValueFor(metric));
        return Gauge.builder(metric + prefix, () -> gaugeValues.get(prefix).get(metric)).register(meterRegistry);
    }

    public void incrementCounter(String prefix, String metric) {
        getCounter(prefix, metric).increment();
    }

    public void incrementCounter(String prefix, String metric, double amount) {
        if (TP_TOTAL_STILLINGSPROSENT_TIME.equalsIgnoreCase(metric)) {
            gaugeValues.get(prefix).put(TP_LATEST_STILLINGSPROSENT_TIME, amount);
        }
        getCounter(prefix, metric).increment(amount);
    }

    public long startTime() {
        return System.currentTimeMillis();
    }

    public long elapsedSince(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    private Counter getCounter(String prefix, String metric) {
        return (Counter) metrics.get(prefix).get(metric);
    }

    public Number getCounterValue(String prefix, String metric) {
        return getCounter(prefix, metric).count();
    }

    public Number getGaugeValue(String prefix, String metric) {
        return getGauge(prefix, metric).value();
    }

    public Gauge getGauge(String prefix, String metric) {
        return (Gauge) metrics.get(prefix).get(metric);
    }

    public class Metrics {
        public static final String APP_NAME = "tjenestepensjon_simulering";
        public static final String TP_TOTAL_STILLINGSPROSENT_CALLS = APP_NAME + "_tp_ordning_calls_";
        public static final String TP_TOTAL_STILLINGSPROSENT_TIME = APP_NAME + "_tp_ordning_time_";
        public static final String TP_LATEST_STILLINGSPROSENT_TIME = APP_NAME + "_tp_ordning_time_latest_";
        public static final String APP_TOTAL_STILLINGSPROSENT_CALLS = APP_NAME + "_app_stillingsprosent_calls";
        public static final String APP_TOTAL_STILLINGSPROSENT_TIME = APP_NAME + "_app_stillingsprosetnt_time";
    }
}
