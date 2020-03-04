package no.nav.tjenestepensjon.simulering.v1.models.domain

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

data class TpForhold(
        var tpnr: String,
        var tssEksternId: String,
        var stillingsprosentListe: List<Stillingsprosent> = emptyList()
) {
    constructor(tpOrdning: TPOrdning, stillingsprosentListe: List<Stillingsprosent>) : this(
            tpnr = tpOrdning.tpId,
            tssEksternId = tpOrdning.tssId,
            stillingsprosentListe = stillingsprosentListe
    )
}