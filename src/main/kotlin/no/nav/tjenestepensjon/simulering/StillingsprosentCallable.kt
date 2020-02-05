package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class StillingsprosentCallable(
        private val fnr: FNR,
        private val tpOrdning: TPOrdning,
        private val tpLeverandor: TpLeverandor,
        private val endpointRouter: TjenestepensjonsimuleringEndpointRouter
) : Callable<List<Stillingsprosent>> {
    override fun call() = try {
        endpointRouter.getStillingsprosenter(fnr, tpOrdning, tpLeverandor)
    } catch (e: Throwable) {
        e.printStackTrace()
        throw StillingsprosentCallableException("Call to getStillingsprosenter failed with exception: $e", e, tpOrdning)
                .also { ex ->
                    LOG.warn("Rethrowing as: {}", ex.toString())
                }
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}