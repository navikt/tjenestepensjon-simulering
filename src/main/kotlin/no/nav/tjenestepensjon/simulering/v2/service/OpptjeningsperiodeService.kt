package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.TPOrdningTpLeverandorMap

interface OpptjeningsperiodeService {
    fun getOpptjeningsperiodeListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): OpptjeningsperiodeResponse
    @Throws(DuplicateOpptjeningsperiodeEndDateException::class, MissingOpptjeningsperiodeException::class)
    fun getLatestFromOpptjeningsperiode(map: TPOrdningOpptjeningsperiodeMap): TPOrdning
}