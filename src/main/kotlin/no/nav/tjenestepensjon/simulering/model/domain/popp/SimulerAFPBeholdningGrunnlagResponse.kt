package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimulerAFPBeholdningGrunnlagResponse(val personId: String, val afpGrunnlagBeholdninger: List<AFPGrunnlagBeholdningPeriode>)
