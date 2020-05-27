package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulerOffentligTjenestepensjonResponse


interface SimuleringService {
    fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest,
                                        stillingsprosentResponse: StillingsprosentResponse,
                                        tpOrdning: TPOrdning,
                                        tpLeverandor: TpLeverandor): SimulerOffentligTjenestepensjonResponse
}