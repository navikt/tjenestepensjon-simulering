package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain

data class SimulertTjenestepensjon(
    var ordningsListe: List<Ordning> = emptyList(),
    var utbetalingsperioder: List<Utbetalingsperiode> = emptyList(),
    var aarsakIngenUtbetaling: List<String> = emptyList(),
)

data class Ordning(val tpNummer: String)