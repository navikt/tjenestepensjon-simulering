package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import org.springframework.stereotype.Component

@Component
class SimulerPensjonServiceImpl : SimulerPensjonService {
    override fun simulerPensjon(tpOrdningList: List<TPOrdning>, tpLatest: TPOrdning): SimulerPensjonResponse {
        return SimulerPensjonResponse(null, null)
    }
}