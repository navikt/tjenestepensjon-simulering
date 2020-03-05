package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class StillingsprosentCallable(
        private val fnr: FNR,
        private val tpOrdning: TPOrdning,
        private val tpLeverandor: TpLeverandor,
        private val endpointRouter: TjenestepensjonsimuleringEndpointRouterOld
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

    operator fun invoke() = call()

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}