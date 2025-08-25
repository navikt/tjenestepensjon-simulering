package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto

data class HentStillingsprosentListeRequest(
        val tssEksternId: String? = null,
        val fnr: FNR? = null,
        val tpnr: String? = null,
        val simuleringsKode: String? = null,
) {
    constructor(fnr: FNR, tpOrdning: TpOrdningFullDto) : this(
            fnr = fnr,
            tpnr = tpOrdning.tpNr,
            tssEksternId = tpOrdning.tssId,
            simuleringsKode = "AP"
    )
}