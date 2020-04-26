package no.nav.tjenestepensjon.simulering.v2.models.response

import no.nav.tjenestepensjon.simulering.v1.models.response.AbstractSimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode

data class SimulerOffentligTjenestepensjonResponse(
        val tpnr: String,
        val navnOrdning: String,
        val inkluderteOrdningerListe: List<String> = emptyList(),
        val leverandorUrl: String? = null,
        val utbetalingsperiodeListe: List<Utbetalingsperiode?> = emptyList()
) : AbstractSimulerOffentligTjenestepensjonResponse()