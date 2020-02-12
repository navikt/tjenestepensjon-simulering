package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning

typealias TPOrdningStillingsprosentMap = Map<TPOrdning, List<Stillingsprosent>>
typealias TPOrdningStillingsprosentCallableMap = Map<TPOrdning, StillingsprosentCallable>
typealias TPOrdningTpLeverandorMap = Map<TPOrdning, TpLeverandor>