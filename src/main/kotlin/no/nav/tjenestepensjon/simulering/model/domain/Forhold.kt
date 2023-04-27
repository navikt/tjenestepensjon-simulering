package no.nav.tjenestepensjon.simulering.model.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Forhold(
    var ordning: String
)

