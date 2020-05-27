package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.TPOrdningTpLeverandorMap
import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpptjeningsperiodeServiceImpl(
        private val stillingsprosentService: StillingsprosentService
) : OpptjeningsperiodeService {

    // Opptjeningsperiode is not implemented yet, so we use the old stillingsprosent and convert it to opptjeningsperiode
    override fun getOpptjeningsperiodeListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap) =
            stillingsprosentService.getStillingsprosentListe(fnr, tpOrdningAndLeverandorMap).let {
                OpptjeningsperiodeResponse(
                        mapStillingsprosentToOpptjeningsperiodeMap(it.tpOrdningStillingsprosentMap),
                        it.exceptions
                )
            }


// This is commented out until opptjeningsperiode have been created in new system
//    override fun getOpptjeningsperiodeListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): OpptjeningsperiodeResponse {
//        val callableMap = toCallableMap(fnr, tpOrdningAndLeverandorMap)
//        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS)
//        val startTime = metrics.startTime()
//        val asyncResponse = asyncExecutor.executeAsync(callableMap)
//        val elapsed = metrics.elapsedSince(startTime)
//        LOG.info("Retrieved all opptjeningsperiodeList in: {} ms", elapsed)
//        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME, elapsed.toDouble())
//        return OpptjeningsperiodeResponse(asyncResponse.resultMap, asyncResponse.exceptions)
//    }


    @Throws(
            DuplicateOpptjeningsperiodeEndDateException::class,
            MissingOpptjeningsperiodeException::class
    )
    override fun getLatestFromOpptjeningsperiode(map: TPOrdningOpptjeningsperiodeMap) =
            map.flatMap { (key, list) ->
                list.map { value ->
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

    fun mapStillingsprosentToOpptjeningsperiodeMap(tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap) =
            tpOrdningStillingsprosentMap.entries.map {
                it.key to mapStillingsprosentToOpptjeningsperiodeList(it.value)
            }.toMap()

    fun mapStillingsprosentToOpptjeningsperiodeList(stillingsprosentList: List<Stillingsprosent>): List<Opptjeningsperiode> {
        return stillingsprosentList.map {
            Opptjeningsperiode(
                    datoFom = it.datoFom,
                    datoTom = it.datoTom,
                    stillingsprosent = it.stillingsprosent,
                    aldersgrense = it.aldersgrense,
                    faktiskHovedlonn = it.faktiskHovedlonn?.toIntOrNull(),
                    stillingsuavhengigTilleggslonn = it.stillingsuavhengigTilleggslonn?.toIntOrNull()
            )
        }
    }

//    private fun toCallableMap(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap) =
//            tpOrdningAndLeverandorMap.map { (tpOrdning, tpLeverandor) ->
//                tpOrdning to OpptjeningsperiodeCallable(fnr, tpOrdning, tpLeverandor, simuleringEndPointRouter)
//            }.toMap()

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}