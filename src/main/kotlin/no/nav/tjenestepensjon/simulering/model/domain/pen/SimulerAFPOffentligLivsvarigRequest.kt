package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.time.LocalDate

data class SimulerAFPOffentligLivsvarigRequest(val fnr: String, val fodselsdato: LocalDate, val fremtidigeInntekter: List<FremtidigInntekt>, val fom: LocalDate)
