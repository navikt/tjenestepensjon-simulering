package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

interface SimulerPensjonService {
    fun simulerPensjon(tpOrdningList: List<TPOrdning>, tpLatest: TPOrdning): SimulerPensjonResponse
}