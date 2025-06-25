package no.nav.tjenestepensjon.simulering.model.domain

import java.time.LocalDate

data class TpOrdningMedDato (
    val tpNr: String,
    val navn: String,
    val datoSistOpptjening: LocalDate?,
)