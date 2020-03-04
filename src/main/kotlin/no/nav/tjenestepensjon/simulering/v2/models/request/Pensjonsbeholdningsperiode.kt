package no.nav.tjenestepensjon.simulering.v2.models.request

class Pensjonsbeholdningsperiode(
        val datoFom: String,
        val pensjonsbeholdning: Double,
        val garantipensjonsbeholdning: Double,
        val garantitilleggsbeholdning: Double?

)