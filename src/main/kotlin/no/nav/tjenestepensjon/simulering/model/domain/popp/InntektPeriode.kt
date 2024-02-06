package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class InntektPeriode(@JsonProperty("fraOgMedDato") val fom: LocalDate, @JsonProperty("arligInntekt") val arligInntekt: Int)