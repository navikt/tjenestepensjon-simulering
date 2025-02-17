package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent

interface Tjenestepensjonsimulering {
    @Throws(Throwable::class)
    fun getStillingsprosenter(
        fnr: String, tpOrdning: TPOrdningIdDto, tpLeverandor: TpLeverandor
    ): List<Stillingsprosent>

}
