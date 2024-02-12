package no.nav.tjenestepensjon.simulering.model.domain.pen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimulerAFPOffentligLivsvarigRequest(val fnr: String, val fodselsdato: LocalDate, val fremtidigeInntekter: List<FremtidigInntekt>, val fom: LocalDate)
