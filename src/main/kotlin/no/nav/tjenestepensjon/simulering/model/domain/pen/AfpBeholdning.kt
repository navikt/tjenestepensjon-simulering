package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.time.LocalDate

data class AfpBeholdning(val belop: Int, val fraOgMed: LocalDate, val tilOgMed: LocalDate)
