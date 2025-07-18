package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class SimulerAFPBeholdningGrunnlagRequest(val personId: String, @param:JsonProperty("uttaksDato") val fom: LocalDate, @param:JsonProperty("fremtidigInntektListe") val inntekter: List<InntektPeriode>)