package no.nav.tjenestepensjon.simulering.model.domain

data class TpOrdningFullDto(
    val navn: String,
    val tpNr: String,
    val datoSistOpptjening: String? = null,
    val tssId: String,
)
