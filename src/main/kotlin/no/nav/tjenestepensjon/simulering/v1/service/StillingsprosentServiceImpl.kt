package no.nav.tjenestepensjon.simulering.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v1.StillingsprosentCallable
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.TPOrdningTpLeverandorMap
import no.nav.tjenestepensjon.simulering.v1.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.v1.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.springframework.stereotype.Component

@Component
class StillingsprosentServiceImpl(
        private val asyncExecutor: AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable>,
        private val soapClient: SoapClient,
        private val metrics: AppMetrics) : StillingsprosentService {
    private val log = KotlinLogging.logger {}

    override fun getStillingsprosentListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): StillingsprosentResponse {
        val callableMap = toCallableMap(fnr, tpOrdningAndLeverandorMap)
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_CALLS)
        val startTime = metrics.startTime()
        val asyncResponse = asyncExecutor.executeAsync(callableMap)
        val elapsed = metrics.elapsedSince(startTime)
        log.info { "Retrieved all stillingsprosenter in: $elapsed ms" }
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_OPPTJENINGSPERIODE_TIME, elapsed.toDouble())
        return StillingsprosentResponse(asyncResponse.resultMap, asyncResponse.exceptions).apply {
            exceptions.forEach { log.warn { "Exception caught while fetching stillingsprosenter: ${it.cause}" } }
        }
    }

    @Throws(
        DuplicateStillingsprosentEndDateException::class,
        MissingStillingsprosentException::class
    )
    override fun getLatestFromStillingsprosent(map: TPOrdningStillingsprosentMap) =
        map.flatMap { (key, list) ->
            list.map { value ->
                key to value
            }
        }.ifEmpty { throw MissingStillingsprosentException("Could not find any stillingsprosent") }
            .reduce(::getLatest).first

    @Throws(DuplicateStillingsprosentEndDateException::class)
    private fun getLatest(latest: Pair<TPOrdningIdDto, Stillingsprosent>, other: Pair<TPOrdningIdDto, Stillingsprosent>) = when {
        latest.second.datoTom == other.second.datoTom -> throw DuplicateStillingsprosentEndDateException("Could not decide latest stillingprosent due to multiple stillingsprosent having the same end date")
        other.second.datoTom == null -> other
        latest.second.datoTom == null || latest.second.datoTom!! > other.second.datoTom -> latest
        latest.second.datoTom!! < other.second.datoTom -> other
        else -> latest
    }

    private fun toCallableMap(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap) =
        tpOrdningAndLeverandorMap.map { (tpOrdning, tpLeverandor) ->
            tpOrdning to StillingsprosentCallable(fnr, tpOrdning, tpLeverandor, soapClient)
        }.toMap()

}