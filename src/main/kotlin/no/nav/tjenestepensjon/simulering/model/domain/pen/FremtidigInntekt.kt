package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.time.LocalDate

data class FremtidigInntekt(val belop: Int, val fraOgMed: LocalDate, val tilOgMed: LocalDate)
