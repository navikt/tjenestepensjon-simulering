package no.nav.tjenestepensjon.simulering.v1

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import java.util.concurrent.Callable

class StillingsprosentCallable(
        private val fnr: FNR,
        private val tpOrdning: TPOrdning,
        private val tpLeverandor: TpLeverandor,
        private val soapClient: SoapClient
) : Callable<List<Stillingsprosent>> {
    private val log = KotlinLogging.logger {}

    override fun call() = try {
        soapClient.getStillingsprosenter(fnr, tpOrdning, tpLeverandor)
    } catch (e: Throwable) {
        throw StillingsprosentCallableException("Call to getStillingsprosenter failed with exception: $e", e, tpOrdning)
                .also { ex ->
                    log.warn { "Rethrowing as: $ex" }
                }
    }

    operator fun invoke() = call()

}