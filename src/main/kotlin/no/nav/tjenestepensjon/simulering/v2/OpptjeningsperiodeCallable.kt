package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.exceptions.OpptjeningsperiodeCallableException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class OpptjeningsperiodeCallable(
        private val fnr: FNR,
        private val tpOrdning: TPOrdning,
        private val tpLeverandor: TpLeverandor,
        private val endpointRouter: TjenestepensjonsimuleringEndpointRouter
) : Callable<List<Opptjeningsperiode>> {
    override fun call() = try {
        endpointRouter.getOpptjeningsperiodeListe(fnr, tpOrdning, tpLeverandor)
    } catch (e: Throwable) {
        e.printStackTrace()
        throw OpptjeningsperiodeCallableException("Call to getOpptjeningsperiode failed with exception: $e", e, tpOrdning)
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