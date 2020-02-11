package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonCreator

data class TpForhold @JsonCreator constructor(
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