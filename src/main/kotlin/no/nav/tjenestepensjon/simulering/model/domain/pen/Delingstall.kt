package no.nav.tjenestepensjon.simulering.model.domain.pen

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Delingstall(
    var alder: Alder,
    var delingstall: Double,
)
