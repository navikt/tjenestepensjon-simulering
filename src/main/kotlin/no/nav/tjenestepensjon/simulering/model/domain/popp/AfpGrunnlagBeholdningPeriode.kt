package no.nav.tjenestepensjon.simulering.model.domain.popp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class AfpGrunnlagBeholdningPeriode(val beholdning: Int, val fraOgMedDato: LocalDate, val tilOgMedDato: LocalDate)
