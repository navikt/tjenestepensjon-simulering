package no.nav.tjenestepensjon.simulering.model.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class HateoasWrapper(@param:JsonProperty("_embedded") val embedded: ForholdWrapper)