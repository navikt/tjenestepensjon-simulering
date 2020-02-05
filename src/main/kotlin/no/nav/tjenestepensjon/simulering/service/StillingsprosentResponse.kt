package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import java.util.concurrent.ExecutionException

class StillingsprosentResponse(
        val tpOrdningStillingsprosentMap: Map<TPOrdning, List<Stillingsprosent>>, val exceptions: List<ExecutionException>)