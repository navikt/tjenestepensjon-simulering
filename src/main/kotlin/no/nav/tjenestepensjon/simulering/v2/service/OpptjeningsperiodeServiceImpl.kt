package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpptjeningsperiodeServiceImpl: OpptjeningsperiodeService {

    override fun getOpptjeningsperiodeListe(stillingsprosentResponse: StillingsprosentResponse) =
            OpptjeningsperiodeResponse(
                    mapStillingsprosentToOpptjeningsperiodeMap(stillingsprosentResponse.tpOrdningStillingsprosentMap),
                    stillingsprosentResponse.exceptions
            )

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

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}