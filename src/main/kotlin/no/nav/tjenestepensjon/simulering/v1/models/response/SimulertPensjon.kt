package no.nav.tjenestepensjon.simulering.v1.models.response

import no.nav.tjenestepensjon.simulering.v1.models.domain.Utbetalingsperiode

data class SimulertPensjon(
        val tpnr: String? = null,
        val navnOrdning: String? = null,
        val inkluderteOrdninger: List<String>? = null,
        val leverandorUrl: String? = null,
        var inkluderteTpnr: List<String>? = null,
        var utelatteTpnr: List<String>? = null,
        val utbetalingsperioder: List<Utbetalingsperiode?>? = null,
        var status: String? = null,
        val feilkode: String? = null,
        val feilbeskrivelse: String? = null
)