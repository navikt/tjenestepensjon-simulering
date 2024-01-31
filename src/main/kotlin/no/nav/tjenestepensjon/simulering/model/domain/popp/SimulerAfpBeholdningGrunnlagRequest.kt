package no.nav.tjenestepensjon.simulering.model.domain.popp

import java.time.LocalDate

data class SimulerAfpBeholdningGrunnlagRequest(val personId: String, val fraOgMedDato: LocalDate, val inntekter: List<InntektPeriode>)