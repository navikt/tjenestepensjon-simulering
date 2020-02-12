package no.nav.tjenestepensjon.simulering.model.v1.request

import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning

data class HentStillingsprosentListeRequest(
        val tssEksternId: String,
        val fnr: FNR,
        val tpnr: String,
        val simuleringsKode: String
) {
    constructor(fnr: FNR, tpOrdning: TPOrdning): this(
                fnr = fnr,
                tpnr = tpOrdning.tpId,
                tssEksternId = tpOrdning.tssId,
                simuleringsKode = "AP"
        )
}