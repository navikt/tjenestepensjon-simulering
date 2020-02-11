package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.util.TPOrdningStillingsprosentMap
import java.util.concurrent.ExecutionException

open class StillingsprosentResponse(
        val tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap,
        val exceptions: List<ExecutionException>
)