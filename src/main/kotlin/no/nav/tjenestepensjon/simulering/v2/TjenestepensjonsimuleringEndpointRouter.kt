package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.v2.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TjenestepensjonsimuleringEndpointRouter(
        private val restClient: RestClient,
        private val metrics: AppMetrics
) {
    fun getStillingsprosenter(fnr: FNR, tpOrdning: TPOrdning, tpLeverandor: TpLeverandor): List<Opptjeningsperiode> {
        metrics.incrementCounter(tpLeverandor.name, AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS)
        val startTime = metrics.startTime()
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().name, tpLeverandor)

        val stillingsprosentList: List<Opptjeningsperiode> = restClient.getOpptjeningsperiode(fnr, tpOrdning, tpLeverandor)

        val elapsed = metrics.elapsedSince(startTime)
        metrics.incrementCounter(tpLeverandor.name, TP_TOTAL_STILLINGSPROSENT_TIME, elapsed.toDouble())
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpLeverandor, elapsed)
        return stillingsprosentList
    }

    fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningOpptjeningsperiodeMap: TPOrdningOpptjeningsperiodeMap
    ): SimulerOffentligTjenestepensjonResponse {
        metrics.incrementCounter(tpLeverandor.name, TP_TOTAL_SIMULERING_CALLS)
        val startTime = metrics.startTime()
        LOG.info("{} getting simulering from: {}", Thread.currentThread().name, tpLeverandor)

        val simulertPensjon = restClient.getResponse(request, tpOrdning, tpLeverandor, tpOrdningOpptjeningsperiodeMap)

        val elapsed = metrics.elapsedSince(startTime)
        metrics.incrementCounter(tpLeverandor.name, TP_TOTAL_SIMULERING_TIME, elapsed.toDouble())
        LOG.info("Retrieved simulation from: {} in: {} ms", tpLeverandor, elapsed)
        return simulertPensjon
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}