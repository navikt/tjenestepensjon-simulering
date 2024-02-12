package no.nav.tjenestepensjon.simulering.model.domain.pen

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class FremtidigInntekt(val belop: Int, @JsonProperty("fraOgMed") val fom: LocalDate)
