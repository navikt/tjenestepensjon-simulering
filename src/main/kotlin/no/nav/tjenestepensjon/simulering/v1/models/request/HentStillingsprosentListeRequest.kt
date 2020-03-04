package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

data class HentStillingsprosentListeRequest(
        val tssEksternId: String,
        val fnr: FNR,
        val tpnr: String,
        val simuleringsKode: String
) {
    constructor(fnr: FNR, tpOrdning: TPOrdning) : this(
            fnr = fnr,
            tpnr = tpOrdning.tpId,
            tssEksternId = tpOrdning.tssId,
            simuleringsKode = "AP"
    )
}