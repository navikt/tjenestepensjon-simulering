package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode

typealias TPOrdningOpptjeningsperiodeMap = Map<TPOrdningIdDto, List<Opptjeningsperiode>>
typealias TPOrdningTpLeverandorMap = Map<TPOrdningIdDto, TpLeverandor>