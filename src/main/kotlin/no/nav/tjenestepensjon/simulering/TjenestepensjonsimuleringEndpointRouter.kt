package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.rest.RestClient
import no.nav.tjenestepensjon.simulering.soap.SoapClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TjenestepensjonsimuleringEndpointRouter(
        private val soapClient: SoapClient,
        private val restClient: RestClient,
        private val metrics: AppMetrics
) {
    fun getStillingsprosenter(fnr: FNR, tpOrdning: TPOrdning, tpLeverandor: TpLeverandor): List<Stillingsprosent> {
        metrics.incrementCounter(tpLeverandor.name, AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS)
        val startTime = metrics.startTime()
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().name, tpLeverandor)

        val stillingsprosentList: List<Stillingsprosent> = when (tpLeverandor.impl) {
            SOAP -> soapClient
            REST -> restClient
        }.getStillingsprosenter(fnr, tpOrdning, tpLeverandor)

        val elapsed = metrics.elapsedSince(startTime)
        metrics.incrementCounter(tpLeverandor.name, TP_TOTAL_STILLINGSPROSENT_TIME, elapsed.toDouble())
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpLeverandor, elapsed)
        return stillingsprosentList
    }

    fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ): List<SimulertPensjon> {
        metrics.incrementCounter(tpLeverandor.name, TP_TOTAL_SIMULERING_CALLS)
        val startTime = metrics.startTime()
        LOG.info("{} getting simulering from: {}", Thread.currentThread().name, tpLeverandor)

        val simulertPensjonList = when (tpLeverandor.impl) {
            SOAP -> soapClient
            REST -> restClient
        }.simulerPensjon(request, tpOrdning, tpLeverandor, tpOrdningStillingsprosentMap)

        val elapsed = metrics.elapsedSince(startTime)
        metrics.incrementCounter(tpLeverandor.name, TP_TOTAL_SIMULERING_TIME, elapsed.toDouble())
        LOG.info("Retrieved simulation from: {} in: {} ms", tpLeverandor, elapsed)
        return simulertPensjonList
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}