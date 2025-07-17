package no.nav.tjenestepensjon.simulering.model.domain.pen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class FremtidigInntekt(val belop: Int, @param:JsonProperty("fraOgMed") val fom: LocalDate)
