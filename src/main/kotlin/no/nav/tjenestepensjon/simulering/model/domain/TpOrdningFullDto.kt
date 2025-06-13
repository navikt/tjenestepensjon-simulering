package no.nav.tjenestepensjon.simulering.model.domain

import java.time.LocalDate

data class TpOrdningFullDto(
    val navn: String,
    val tpNr: String,
    val datoSistOpptjening: LocalDate? = null,
    val tssId: String,
)
