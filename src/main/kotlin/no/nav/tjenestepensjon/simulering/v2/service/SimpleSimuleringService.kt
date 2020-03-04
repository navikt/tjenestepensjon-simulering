package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_OK
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v2.TjenestepensjonsimuleringEndpointRouter
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class SimpleSimuleringService(
        private val simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter,
        private val stillingsprosentService: StillingsprosentService,
        private val tpConfigConsumer: TpConfigConsumer,
        private val tpLeverandorList: List<TpLeverandor>,
        private val tpRegisterConsumer: TpRegisterConsumer,
        private val asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>,
        private val metrics: AppMetrics
) : SimuleringService {

    override fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest): SimulerOffentligTjenestepensjonResponse {

        val tpOrdningAndLeverandorMap = tpRegisterConsumer.getTpOrdningerForPerson(request.fnr)
                .let(::getTpLeverandorer)

        val stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(request.fnr, tpOrdningAndLeverandorMap)

        return stillingsprosentResponse.tpOrdningStillingsprosentMap
                .ifEmpty { throw NoTpOrdningerFoundException("Could not get stillingsprosent from any TP-Providers") }
                .let(stillingsprosentService::getLatestFromStillingsprosent)
                .let { tpOrdning ->
                    simuleringEndPointRouter.simulerPensjon(
                            request = request,
                            tpOrdning = tpOrdning,
                            tpLeverandor = tpOrdningAndLeverandorMap.getValue(tpOrdning),
                            tpOrdningStillingsprosentMap = stillingsprosentResponse.tpOrdningStillingsprosentMap
                    )
                }.also { response ->
                    addResponseInfoWhenSimulert(
                            response,
                            stillingsprosentResponse
                    )
                }
    }

    private fun addResponseInfoWhenSimulert( //todo do we need thos f(x)?
            response: SimulerOffentligTjenestepensjonResponse,
            stillingsprosentResponse: StillingsprosentResponse
    ) {
        val utelatteTpNr = stillingsprosentResponse.exceptions
                .map(ExecutionException::cause)
                .filterIsInstance<StillingsprosentCallableException>()
                .map(StillingsprosentCallableException::tpOrdning)
                .map(TPOrdning::tpId)

        incrementMetrics(response, utelatteTpNr)
    }

    private fun incrementMetrics( //todo do we need thos fx?
            response: SimulerOffentligTjenestepensjonResponse?,
            utelatteTpNr: List<String?>
    ) {
        val ufullstendig = utelatteTpNr.isNotEmpty()
        val mangelfull = response?.utbetalingsperiodeListe.isNullOrEmpty()

        if (ufullstendig)
            metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)
        if (mangelfull)
            metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)
        if (!ufullstendig && !mangelfull)
            metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_OK)
    }

    private fun getTpLeverandorer(tpOrdningList: List<TPOrdning>) =
            asyncExecutor.executeAsync(
                    tpOrdningList.map { tpOrdning ->
                        tpOrdning to FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorList)
                    }.toMap()
            ).resultMap

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}