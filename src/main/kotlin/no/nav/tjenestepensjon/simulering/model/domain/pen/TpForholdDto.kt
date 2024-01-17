package no.nav.tjenestepensjon.simulering.model.domain.pen

data class TpForholdDto (
    val tpnr: String,
    val opptjeningsperiodeListe: List<OpptjeningsperiodeDto> = emptyList(),
)
