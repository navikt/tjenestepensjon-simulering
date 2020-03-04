package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon

interface Tjenestepensjonsimulering {
    @Throws(Throwable::class)
    fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ): List<Stillingsprosent>

    fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ): List<SimulertPensjon>
}