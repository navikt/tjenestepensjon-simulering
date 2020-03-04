package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.StillingsprosentCallable

typealias TPOrdningStillingsprosentMap = Map<TPOrdning, List<Stillingsprosent>>
typealias TPOrdningStillingsprosentCallableMap = Map<TPOrdning, StillingsprosentCallable>
typealias TPOrdningTpLeverandorMap = Map<TPOrdning, TpLeverandor>