package no.nav.tjenestepensjon.simulering.model.v1.response

import no.nav.tjenestepensjon.simulering.model.v1.domain.Utbetalingsperiode

data class SimulertPensjonOK(
        val tpnr: String,
        val navnOrdning: String,
        val inkluderteOrdninger: List<String> = emptyList(),
        val leverandorUrl: String? = null,
        var inkluderteTpnr: List<String> = emptyList(),
        var utelatteTpnr: List<String> = emptyList(),
        val utbetalingsperioder: List<Utbetalingsperiode?>
): SimulertPensjon()