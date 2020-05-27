package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

class StillingsprosentCallable(
        private val fnr: FNR,
        private val tpOrdning: TPOrdning,
        private val tpLeverandor: TpLeverandor,
        private val soapClient: SoapClient
) : Callable<List<Stillingsprosent>> {
    override fun call() = try {
        soapClient.getStillingsprosenter(fnr, tpOrdning, tpLeverandor)
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