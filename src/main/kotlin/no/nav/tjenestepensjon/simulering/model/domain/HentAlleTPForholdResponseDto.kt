package no.nav.tjenestepensjon.simulering.model.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class HentAlleTPForholdResponseDto(
    val fnr: String,
    val forhold: List<TPForholdResponseDto>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TPForholdResponseDto(
    val tpNr: String,
    val tpOrdningNavn: String?,
    val datoSistOpptjening: LocalDate?,
)