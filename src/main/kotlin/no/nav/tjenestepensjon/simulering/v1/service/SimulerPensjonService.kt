package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto

interface SimulerPensjonService {
    fun simulerPensjon(tpOrdningIdDtoList: List<TPOrdningIdDto>, tpLatest: TPOrdningIdDto): SimulerPensjonResponse
}