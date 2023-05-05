package no.nav.tjenestepensjon.simulering.model.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class HateoasWrapper(@JsonProperty("_embedded") val embedded: ForholdWrapper)