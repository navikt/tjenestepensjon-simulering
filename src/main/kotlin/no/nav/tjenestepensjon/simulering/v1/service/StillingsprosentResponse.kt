package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import java.util.concurrent.ExecutionException

class StillingsprosentResponse(
        val tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap,
        val exceptions: List<ExecutionException>
)