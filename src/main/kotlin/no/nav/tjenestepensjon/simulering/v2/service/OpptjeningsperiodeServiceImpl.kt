package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.OpptjeningsperiodeCallable
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.TPOrdningTpLeverandorMap
import no.nav.tjenestepensjon.simulering.v2.TjenestepensjonsimuleringEndpointRouter
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpptjeningsperiodeServiceImpl(
        private val asyncExecutor: AsyncExecutor<List<Opptjeningsperiode>, OpptjeningsperiodeCallable>,
        private val simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter,
        private val metrics: AppMetrics) : OpptjeningsperiodeService
{

    override fun getOpptjeningsperiodeListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): OpptjeningsperiodeResponse {
        val callableMap = toCallableMap(fnr, tpOrdningAndLeverandorMap)
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS)
        val startTime = metrics.startTime()
        val asyncResponse = asyncExecutor.executeAsync(callableMap)
        val elapsed = metrics.elapsedSince(startTime)
        LOG.info("Retrieved all opptjeningsperiodeList in: {} ms", elapsed)
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME, elapsed.toDouble())
        return OpptjeningsperiodeResponse(asyncResponse.resultMap, asyncResponse.exceptions)
    }

    @Throws(DuplicateOpptjeningsperiodeEndDateException::class,
            MissingOpptjeningsperiodeException::class)
    override fun getLatestFromOpptjeningsperiode(map: TPOrdningOpptjeningsperiodeMap) =
            map.flatMap { (key, list) ->
                    list.map { value ->
                        LOG.info("TPORDNING {} STILLINGSPROSENT {}", key, value)
                        key to value
                    }
            }.ifEmpty { throw MissingOpptjeningsperiodeException("Could not find any stillingsprosent") }
            .reduce(::getLatest).first

    @Throws(DuplicateOpptjeningsperiodeEndDateException::class)
    private fun getLatest(latest: Pair<TPOrdning, Opptjeningsperiode>, other: Pair<TPOrdning, Opptjeningsperiode>) = when {
            latest.second.datoTom == other.second.datoTom -> throw DuplicateOpptjeningsperiodeEndDateException("Could not decide latest stillingprosent due to multiple stillingsprosent having the same end date")
            other.second.datoTom == null -> other
            latest.second.datoTom == null || latest.second.datoTom!! > other.second.datoTom -> latest
            latest.second.datoTom!! < other.second.datoTom -> other
            else -> latest
        }

    private fun toCallableMap(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap) =
            tpOrdningAndLeverandorMap.map { (tpOrdning, tpLeverandor) ->
                tpOrdning to OpptjeningsperiodeCallable(fnr, tpOrdning, tpLeverandor, simuleringEndPointRouter)
            }.toMap()

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}