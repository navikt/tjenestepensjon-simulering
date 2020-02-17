package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.StillingsprosentCallable
import no.nav.tjenestepensjon.simulering.TjenestepensjonsimuleringEndpointRouter
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.TPOrdningTpLeverandorMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StillingsprosentServiceImpl(
        private val asyncExecutor: AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable>,
        private val simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter,
        private val metrics: AppMetrics) : StillingsprosentService
{

    override fun getStillingsprosentListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): StillingsprosentResponse {
        val callableMap = toCallableMap(fnr, tpOrdningAndLeverandorMap)
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS)
        val startTime = metrics.startTime()
        val asyncResponse = asyncExecutor.executeAsync(callableMap)
        val elapsed = metrics.elapsedSince(startTime)
        LOG.info("Retrieved all stillingsprosenter in: {} ms", elapsed)
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME, elapsed.toDouble())
        return StillingsprosentResponse(asyncResponse.resultMap, asyncResponse.exceptions)
    }

    @Throws(DuplicateStillingsprosentEndDateException::class,
            MissingStillingsprosentException::class)
    override fun getLatestFromStillingsprosent(map: TPOrdningStillingsprosentMap) =
            map.flatMap { (key, list) ->
                list.map { value ->
                    LOG.info("TPORDNING {} STILLINGSPROSENT {}", key, value)
                    key to value
                }
            }.ifEmpty { throw MissingStillingsprosentException("Could not find any stillingsprosent") }
            .reduce(::getLatest).first

    @Throws(DuplicateStillingsprosentEndDateException::class)
    private fun getLatest(latest: Pair<TPOrdning, Stillingsprosent>, other: Pair<TPOrdning, Stillingsprosent>) = when {
            latest.second.datoTom == other.second.datoTom -> throw DuplicateStillingsprosentEndDateException("Could not decide latest stillingprosent due to multiple stillingsprosent having the same end date")
            other.second.datoTom == null -> other
            latest.second.datoTom == null || latest.second.datoTom!! > other.second.datoTom -> latest
            latest.second.datoTom!! < other.second.datoTom -> other
            else -> latest
        }

    private fun toCallableMap(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap) =
            tpOrdningAndLeverandorMap.map { (tpOrdning, tpLeverandor) ->
                tpOrdning to StillingsprosentCallable(fnr, tpOrdning, tpLeverandor, simuleringEndPointRouter)
            }.toMap()

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}