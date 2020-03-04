package no.nav.tjenestepensjon.simulering.v2.models.request

class Simuleringsperiode(
        val datoFom: String,
        var folketrygdUttaksgrad: Int,
        var stillingsprosentOffentlig: Int,
        var simulerAFPOffentligEtterfulgtAvAlderListe: Boolean
)