package no.nav.tjenestepensjon.simulering.model.v1.response

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.Utbetalingsperiode

data class SimulertPensjon @JsonCreator constructor(
    var tpnr: String,
    var navnOrdning: String,
    var inkluderteOrdninger: List<String> = emptyList(),
    var leverandorUrl: String? = null,
    var inkluderteTpnr: List<String> = emptyList(),
    var utelatteTpnr: List<String> = emptyList(),
    var status: String? = null,
    var feilkode: String? = null,
    var feilbeskrivelse: String? = null,
    var utbetalingsperioder: List<Utbetalingsperiode?>
)