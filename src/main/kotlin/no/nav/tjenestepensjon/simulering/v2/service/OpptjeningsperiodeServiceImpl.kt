package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import org.springframework.stereotype.Component

@Component
class OpptjeningsperiodeServiceImpl: OpptjeningsperiodeService {

    override fun getOpptjeningsperiodeListe(tpOrdning: TpOrdningFullDto, stillingsprosentListe: List<Stillingsprosent>) =
            OpptjeningsperiodeResponse(
                    mapOf(tpOrdning to mapStillingsprosentToOpptjeningsperiodeList(stillingsprosentListe)),
                    emptyList()
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
    private fun getLatest(latest: Pair<TpOrdningFullDto, Opptjeningsperiode>, other: Pair<TpOrdningFullDto, Opptjeningsperiode>) = when {
        latest.second.datoTom == other.second.datoTom -> throw DuplicateOpptjeningsperiodeEndDateException("Could not decide latest stillingprosent due to multiple stillingsprosent having the same end date")
        other.second.datoTom == null -> other
        latest.second.datoTom == null || latest.second.datoTom!! > other.second.datoTom -> latest
        latest.second.datoTom!! < other.second.datoTom -> other
        else -> latest
    }

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
}