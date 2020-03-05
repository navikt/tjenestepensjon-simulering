package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.v2.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode

typealias TPOrdningOpptjeningsperiodeMap = Map<TPOrdning, List<Opptjeningsperiode>>
typealias TPOrdningStillingsprosentCallableMap = Map<TPOrdning, OpptjeningsperiodeCallable> //todo might need to remove
typealias TPOrdningTpLeverandorMap = Map<TPOrdning, TpLeverandor>