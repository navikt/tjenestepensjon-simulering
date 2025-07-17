package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimulerAFPBeholdningGrunnlagResponse(@param:JsonProperty("afpBeholdningsgrunnlag") val pensjonsBeholdningsPeriodeListe: List<AFPGrunnlagBeholdningPeriode>)
