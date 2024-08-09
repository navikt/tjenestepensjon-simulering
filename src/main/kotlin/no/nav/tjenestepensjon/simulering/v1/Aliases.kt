package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto

typealias TPOrdningStillingsprosentMap = Map<TPOrdningIdDto, List<Stillingsprosent>>
typealias TPOrdningStillingsprosentCallableMap = Map<TPOrdningIdDto, StillingsprosentCallable>
typealias TPOrdningTpLeverandorMap = Map<TPOrdningIdDto, TpLeverandor>