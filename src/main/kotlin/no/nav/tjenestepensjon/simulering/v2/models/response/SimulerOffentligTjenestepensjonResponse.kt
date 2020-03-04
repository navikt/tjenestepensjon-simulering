package no.nav.tjenestepensjon.simulering.v2.models.response

import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode

data class SimulerOffentligTjenestepensjonResponse(
        val tpnr: String,
        val navnOrdning: String,
        val inkluderteOrdningeListe: List<String>? = null,
        val leverandorUrl: String? = null,
        val utbetalingsperiodeListe: List<Utbetalingsperiode?>? = null
)