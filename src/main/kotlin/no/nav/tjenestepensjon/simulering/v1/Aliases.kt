package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.v1.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

typealias TPOrdningStillingsprosentMap = Map<TPOrdning, List<Stillingsprosent>>
typealias TPOrdningStillingsprosentCallableMap = Map<TPOrdning, StillingsprosentCallable>
typealias TPOrdningTpLeverandorMap = Map<TPOrdning, TpLeverandor>