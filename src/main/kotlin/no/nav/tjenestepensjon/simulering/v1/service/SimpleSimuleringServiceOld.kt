package no.nav.tjenestepensjon.simulering.v1.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_OK
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.v1.TjenestepensjonsimuleringEndpointRouterOld
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException
import no.nav.tjenestepensjon.simulering.v1.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent.ExecutionException

@Service
class SimpleSimuleringServiceOld(
        private val simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouterOld,
        private val stillingsprosentService: StillingsprosentService,
        private val tpConfigConsumer: TpConfigConsumer,
        private val tpLeverandorList: List<TpLeverandor>,
        private val tpRegisterConsumer: TpRegisterConsumer,
        private val asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>,
        private val metrics: AppMetrics
) : SimuleringService {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    override fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest) =
            SimulerOffentligTjenestepensjonResponse(
                    simulertPensjonListe = try {
                        val tpOrdningAndLeverandorMap = tpRegisterConsumer.getTpOrdningerForPerson(request.fnr)
                                .let(::getTpLeverandorer)
                        val stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(request.fnr, tpOrdningAndLeverandorMap)

                        LOG.error("/////////////////v1///////////////////////////")
                        LOG.error("tpOrdningAndLeverandorMap: {}", objectMapper.writeValueAsString(tpOrdningAndLeverandorMap))
                        LOG.error("opptjeningsperiodeResponse: {}", objectMapper.writeValueAsString(stillingsprosentResponse))
                        LOG.error("/////////////////v1///////////////////////////")

                        stillingsprosentResponse.tpOrdningStillingsprosentMap
                                .ifEmpty { throw NoTpOrdningerFoundException("Could not get stillingsprosent from any TP-Providers") }
                                .let(stillingsprosentService::getLatestFromStillingsprosent)
                                .let { tpOrdning ->
                                    simuleringEndPointRouter.simulerPensjon(
                                            request = request,
                                            tpOrdning = tpOrdning,
                                            tpLeverandor = tpOrdningAndLeverandorMap[tpOrdning]!!,
                                            tpOrdningStillingsprosentMap = stillingsprosentResponse.tpOrdningStillingsprosentMap
                                    )
                                }.let { simulertPensjonList ->
                                    addResponseInfoWhenSimulert(
                                            simulertPensjonList,
                                            stillingsprosentResponse
                                    )
                                }
                    } catch (e: SimuleringException) {
                        addResponseInfoWhenError(e)
                    }
            )

    private fun addResponseInfoWhenError(e: SimuleringException): List<SimulertPensjon> {
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_FEIL)
        return listOf(SimulertPensjon(
                feilkode = e.feilkode,
                feilbeskrivelse = e.message,
                status = "FEIL"
        ))
    }

    private fun addResponseInfoWhenSimulert(simulertPensjonList: List<SimulertPensjon>, stillingsprosentResponse: StillingsprosentResponse): List<SimulertPensjon> {
        val utelatteTpNr = stillingsprosentResponse.exceptions
                .map(ExecutionException::cause)
                .filterIsInstance<StillingsprosentCallableException>()
                .map(StillingsprosentCallableException::tpOrdning)
                .map(TPOrdning::tpId)
        val inkluderteTpNr = stillingsprosentResponse.tpOrdningStillingsprosentMap.keys
                .map(TPOrdning::tpId)
        return simulertPensjonList.onEach { simulertPensjon ->
            if (simulertPensjon.tpnr != null) {
                simulertPensjon.utelatteTpnr = utelatteTpNr
                simulertPensjon.inkluderteTpnr = inkluderteTpNr
                if (utelatteTpNr.isNotEmpty())
                    simulertPensjon.status = "UFUL"
            }
        }.also {
            incrementMetrics(it, utelatteTpNr)
        }
    }

    private fun incrementMetrics(simulertPensjonList: List<SimulertPensjon>, utelatteTpNr: List<String?>) {
        val ufullstendig = utelatteTpNr.isNotEmpty()
        val mangelfull = simulertPensjonList.flatMap { it.utbetalingsperioder ?: emptyList() }.any { it == null }

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