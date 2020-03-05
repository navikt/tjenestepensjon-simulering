package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import java.util.concurrent.ExecutionException

class OpptjeningsperiodeResponse(
        val tpOrdningOpptjeningsperiodeMap: TPOrdningOpptjeningsperiodeMap,
        val exceptions: List<ExecutionException>
)