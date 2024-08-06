package no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.exceptions.LeveradoerNotFoundException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.domain.SisteOrdning
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SisteOrdningService(
    private val tpClient: TpClient,
    private val stillingsprosentService: StillingsprosentService,
    private val metrics: AppMetrics,
    private val asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>,
    @Qualifier("tpLeverandor2025") private val tpLeverandor2025List: List<TpLeverandor>,
    ) {

    fun finnSisteOrdning(fnr: String) : SisteOrdning {
        val tpOrdningAndLeverandorMap = tpClient.findForhold(fnr)
            .mapNotNull { forhold -> tpClient.findTssId(forhold.ordning)?.let { TPOrdning(tpId = forhold.ordning, tssId = it) } }//tpClient.findTpLeverandorName(tpOrdning)
            .let(::getTpLeverandorer)
        val stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(fnr, tpOrdningAndLeverandorMap)
        val tpOrdning = stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.tpOrdningStillingsprosentMap)

        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_OK)
        val tpLeverandor = tpOrdningAndLeverandorMap[tpOrdning]!!

        return SisteOrdning(tpOrdning.tpId, tpLeverandor.name, tpLeverandor.simuleringUrl)
    }

    private fun getTpLeverandorer(tpOrdningList: List<TPOrdning>): MutableMap<TPOrdning, TpLeverandor> {
        if (tpOrdningList.isEmpty()) throw LeveradoerNotFoundException("TSSnr not found for any tpOrdning.")
        return asyncExecutor.executeAsync(tpOrdningList.associateWith { tpOrdning ->
            FindTpLeverandorCallable(tpOrdning, tpClient, tpLeverandor2025List, metrics)
        }).resultMap.apply {
            if (isEmpty()) throw LeveradoerNotFoundException("No Tp-leverandoer found for person.")
        }
    }

}