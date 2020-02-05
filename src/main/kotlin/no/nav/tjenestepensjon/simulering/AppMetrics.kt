package no.nav.tjenestepensjon.simulering

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_OK
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_LATEST_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_LATEST_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer

@Component
class AppMetrics(private val meterRegistry: MeterRegistry, tpLeverandorList: List<TpLeverandor>) {
    private val metrics: MutableMap<String, Map<String, Meter>> = generateMetrics(tpLeverandorList)
    private val gaugeValues: MutableMap<String, MutableMap<String, Number>> = HashMap()

    private fun generateMetrics(tpLeverandorList: List<TpLeverandor>) =
            (tpLeverandorList.map { tpLeverandor -> tpLeverandor.name to metricsFor(tpLeverandor) }
                    + (APP_NAME to mapOf(
                    APP_TOTAL_STILLINGSPROSENT_CALLS to createCounter(APP_TOTAL_STILLINGSPROSENT_CALLS, "Totalt antall kall mot stillingsprosent"),
                    APP_TOTAL_STILLINGSPROSENT_TIME to createCounter(APP_TOTAL_STILLINGSPROSENT_TIME, "Akkumulert responstid for alle kall mot stillingsprosent"),
                    APP_TOTAL_SIMULERING_CALLS to createCounter(APP_TOTAL_SIMULERING_CALLS, "Totalt antall kall til endepunkt for simulering"),
                    APP_TOTAL_SIMULERING_TIME to createCounter(APP_TOTAL_SIMULERING_TIME, "Akkumulert responstid for simulering"),
                    APP_TOTAL_SIMULERING_OK to createCounter(APP_TOTAL_SIMULERING_OK, "Totalt antall fullstendige simuleringer"),
                    APP_TOTAL_SIMULERING_UFUL to createCounter(APP_TOTAL_SIMULERING_UFUL, "Totalt antall ufullstendige simuleringer grunnet kommunikasjonsproblemer mot TP Leverandør"),
                    APP_TOTAL_SIMULERING_FEIL to createCounter(APP_TOTAL_SIMULERING_FEIL, "Totalt antall som ikke kunne simuleres"),
                    APP_TOTAL_SIMULERING_MANGEL to createCounter(APP_TOTAL_SIMULERING_MANGEL, "Totalt antall simuleringer med kode for mangelfull simulering")
            ))).toMap().toMutableMap()

    private fun metricsFor(tpLeverandor: TpLeverandor) = mapOf(
            TP_TOTAL_STILLINGSPROSENT_CALLS to createCounter(TP_TOTAL_STILLINGSPROSENT_CALLS + tpLeverandor.name, "Totalt antall kall mot stillingsprosent for aktuell tp-leverandør"),
            TP_TOTAL_STILLINGSPROSENT_TIME to createCounter(TP_TOTAL_STILLINGSPROSENT_TIME + tpLeverandor.name, "Akkumulert responstid for aktuell tp-leverandør"),
            TP_LATEST_STILLINGSPROSENT_TIME to createGauge(tpLeverandor.name, TP_LATEST_STILLINGSPROSENT_TIME),
            TP_TOTAL_SIMULERING_CALLS to createCounter(TP_TOTAL_SIMULERING_CALLS + tpLeverandor.name, "Totalt antall kall til simulering hos aktuell tp-leverandør"),
            TP_TOTAL_SIMULERING_TIME to createCounter(TP_TOTAL_SIMULERING_TIME + tpLeverandor.name, "Akkumulert responstid for simulering hos aktuell tp-leverandør"),
            TP_LATEST_SIMULERING_TIME to createGauge(tpLeverandor.name, TP_LATEST_SIMULERING_TIME)
    )

    private fun createCounter(id: String, description: String): Meter {
        return Counter.builder(id).description(description).register(meterRegistry)
    }

    private fun createGauge(prefix: String, metric: String): Gauge {
        gaugeValues[prefix] = mutableMapOf<String, Number>(metric to 0)
        return Gauge.builder(metric + prefix) { gaugeValues[prefix]!![metric] }.register(meterRegistry)
    }

    fun incrementCounter(prefix: String, metric: String) {
        getCounter(prefix, metric)!!.increment()
    }

    fun incrementCounter(prefix: String, metric: String, amount: Double) {
        if (TP_TOTAL_STILLINGSPROSENT_TIME.equals(metric, ignoreCase = true)) {
            gaugeValues[prefix]!![TP_LATEST_STILLINGSPROSENT_TIME] = amount
        } else if (TP_TOTAL_SIMULERING_TIME.equals(metric, ignoreCase = true)) {
            gaugeValues[prefix]!![TP_LATEST_SIMULERING_TIME] = amount
        }
        getCounter(prefix, metric)!!.increment(amount)
    }

    fun startTime(): Long {
        return System.currentTimeMillis()
    }

    fun elapsedSince(startTime: Long): Long {
        return System.currentTimeMillis() - startTime
    }

    private fun getCounter(prefix: String, metric: String) = metrics[prefix]!![metric] as Counter?

    fun getCounterValue(prefix: String, metric: String): Number = getCounter(prefix, metric)!!.count()

    fun getGaugeValue(prefix: String, metric: String): Number = getGauge(prefix, metric)!!.value()

    fun getGauge(prefix: String, metric: String): Gauge? = metrics[prefix]!![metric] as Gauge?

    object Metrics {
        const val APP_NAME = "tjenestepensjon_simulering"
        const val TP_TOTAL_STILLINGSPROSENT_CALLS = APP_NAME + "_tp_leverandor_calls_"
        const val TP_TOTAL_STILLINGSPROSENT_TIME = APP_NAME + "_tp_leverandor_time_"
        const val TP_LATEST_STILLINGSPROSENT_TIME = APP_NAME + "_tp_leverandor_time_latest_"
        const val APP_TOTAL_STILLINGSPROSENT_CALLS = APP_NAME + "_app_stillingsprosent_calls"
        const val APP_TOTAL_STILLINGSPROSENT_TIME = APP_NAME + "_app_stillingsprosent_time"
        const val APP_TOTAL_SIMULERING_CALLS = APP_NAME + "_app_simulering_calls"
        const val APP_TOTAL_SIMULERING_TIME = APP_NAME + "_app_simulering_time"
        const val APP_TOTAL_SIMULERING_OK = APP_NAME + "_app_simulering_ok"
        const val APP_TOTAL_SIMULERING_UFUL = APP_NAME + "_app_simulering_ufullstendig"
        const val APP_TOTAL_SIMULERING_FEIL = APP_NAME + "_app_simulering_feil"
        const val APP_TOTAL_SIMULERING_MANGEL = APP_NAME + "_app_simulering_mangel"
        const val TP_TOTAL_SIMULERING_CALLS = APP_NAME + "_tp_simulering_calls_"
        const val TP_TOTAL_SIMULERING_TIME = APP_NAME + "_tp_simulering_time_"
        const val TP_LATEST_SIMULERING_TIME = APP_NAME + "_tp_simulering_time_latest_"
    }
}