package no.nav.tjenestepensjon.simulering.model.domain

import java.time.LocalDate

data class HentAlleTPForholdResponseDto(
    val fnr: String,
    val forhold: List<TPForholdResponseDto>,
)

data class TPForholdResponseDto(
    val tpNr: String,
    val ordningNavn: String?,
    val datoSistOpptjening: LocalDate?,
)