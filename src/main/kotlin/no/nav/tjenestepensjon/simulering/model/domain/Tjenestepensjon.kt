package no.nav.tjenestepensjon.simulering.model.domain

data class Tjenestepensjon(
    var forhold: List<Forhold>
) {
    data class Forhold(
        var ordning: String
    )
}
