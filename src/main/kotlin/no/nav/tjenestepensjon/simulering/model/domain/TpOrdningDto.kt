package no.nav.tjenestepensjon.simulering.model.domain

data class TpOrdningDto(
    val navn: String,
    val tpNr: String,
    val orgNr: String,
    val alias: List<String>,
)
