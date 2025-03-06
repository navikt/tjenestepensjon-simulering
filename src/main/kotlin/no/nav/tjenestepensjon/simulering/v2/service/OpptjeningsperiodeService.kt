package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException

interface OpptjeningsperiodeService {
    fun getOpptjeningsperiodeListe(tpOrdning: TPOrdningIdDto, stillingsprosentListe: List<Stillingsprosent>): OpptjeningsperiodeResponse
    @Throws(DuplicateOpptjeningsperiodeEndDateException::class, MissingOpptjeningsperiodeException::class)
    fun getLatestFromOpptjeningsperiode(map: TPOrdningOpptjeningsperiodeMap): TPOrdningIdDto
}