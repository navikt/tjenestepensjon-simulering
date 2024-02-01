package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class AFPGrunnlagBeholdningPeriode(val beholdning: Int,  @JsonProperty("fraOgMedDato") val fom: LocalDate, @JsonProperty("tilOgMedDato") val tom: LocalDate)
