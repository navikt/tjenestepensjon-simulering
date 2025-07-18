package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class AFPGrunnlagBeholdningPeriode(@param:JsonProperty("belop") val pensjonsBeholdning: Int, @param:JsonProperty("fraOgMedDato") val fom: LocalDate)
