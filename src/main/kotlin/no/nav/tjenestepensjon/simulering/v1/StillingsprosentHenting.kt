package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent

interface StillingsprosentHenting {
    @Throws(Throwable::class)
    fun getStillingsprosenter(
        fnr: String, tpOrdning: TpOrdningFullDto
    ): List<Stillingsprosent>

}
