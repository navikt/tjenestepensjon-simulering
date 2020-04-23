package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_OK
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.TjenestepensjonsimuleringEndpointRouter
import no.nav.tjenestepensjon.simulering.v2.config.TpLeverandorConfig
import no.nav.tjenestepensjon.simulering.v2.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.v2.exceptions.NoTpOpptjeningsPeriodeFoundException
import no.nav.tjenestepensjon.simulering.v2.exceptions.NoTpParticipantFoundInMapForVersion2
import no.nav.tjenestepensjon.simulering.v2.exceptions.OpptjeningsperiodeCallableException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class SimpleSimuleringService(
        private val simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter,
        private val opptjeningsperiodeService: OpptjeningsperiodeService,
        private val tpConfigConsumer: TpConfigConsumer,
        private val tpLeverandorList: List<TpLeverandor>,
        private val tpRegisterConsumer: TpRegisterConsumer,
        private val asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>,
        private val metrics: AppMetrics,
        private val tpLeverandorConfig: TpLeverandorConfig
) : SimuleringService {

    override fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest): SimulerOffentligTjenestepensjonResponse {
        val test: Map<TPOrdning, List<Opptjeningsperiode>> = mapOf()
        return simuleringEndPointRouter.simulerPensjon(
                request = request,
                tpOrdning = TPOrdning("bogus", "bogus"),
                tpLeverandor = TpLeverandor("KLP", "https://partner-gw-test2.klp.no/api/pensjonsimulering", null, true),
                tpOrdningOpptjeningsperiodeMap = test
        )

    }

    private fun addResponseInfoWhenSimulert(
            response: SimulerOffentligTjenestepensjonResponse,
            opptjeningsperiodeResponse: OpptjeningsperiodeResponse
    ) {
        val utelatteTpNr = opptjeningsperiodeResponse.exceptions
                .map(ExecutionException::cause)
                .filterIsInstance<OpptjeningsperiodeCallableException>()
                .map(OpptjeningsperiodeCallableException::tpOrdning)
                .map(TPOrdning::tpId)

        val ufullstendig = utelatteTpNr.isNotEmpty()
        val mangelfull = response.utbetalingsperiodeListe.isNullOrEmpty()

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