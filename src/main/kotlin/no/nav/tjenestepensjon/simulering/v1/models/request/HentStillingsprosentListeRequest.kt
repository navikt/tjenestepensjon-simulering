package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto

data class HentStillingsprosentListeRequest(
        val tssEksternId: String,
        val fnr: FNR,
        val tpnr: String,
        val simuleringsKode: String
) {
    constructor(fnr: FNR, tpOrdning: TpOrdningFullDto) : this(
            fnr = fnr,
            tpnr = tpOrdning.tpNr,
            tssEksternId = tpOrdning.tssId,
            simuleringsKode = "AP"
    )
}