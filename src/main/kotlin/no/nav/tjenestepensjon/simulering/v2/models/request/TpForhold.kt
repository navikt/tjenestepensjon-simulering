package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode

class TpForhold(
        val tpnr: String,
        val opptjeningsperiodeListe: List<Opptjeningsperiode>
)