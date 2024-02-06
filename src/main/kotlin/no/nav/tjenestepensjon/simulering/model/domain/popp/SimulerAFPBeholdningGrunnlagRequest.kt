package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class SimulerAFPBeholdningGrunnlagRequest(val personId: String, @JsonProperty("fraOgMedDato") val fom: LocalDate, @JsonProperty("fremtidigInntektListe") val inntekter: List<InntektPeriode>)