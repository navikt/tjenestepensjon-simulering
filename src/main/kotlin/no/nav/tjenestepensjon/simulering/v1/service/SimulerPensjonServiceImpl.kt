package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import org.springframework.stereotype.Component

@Component
class SimulerPensjonServiceImpl : SimulerPensjonService {
    override fun simulerPensjon(tpOrdningIdDtoList: List<TPOrdningIdDto>, tpLatest: TPOrdningIdDto): SimulerPensjonResponse {
        return SimulerPensjonResponse(null, null)
    }
}