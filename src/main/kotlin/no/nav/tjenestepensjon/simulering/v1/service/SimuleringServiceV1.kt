package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class SimuleringServiceV1(
    private val soapClient: SoapClient, private val metrics: AppMetrics
) {

    fun simulerOffentligTjenestepensjon(
        request: SimulerPensjonRequestV1,
        stillingsprosentResponse: StillingsprosentResponse,
        tpOrdning: TPOrdning,
        tpLeverandor: TpLeverandor
    ) = SimulerOffentligTjenestepensjonResponse(simulertPensjonListe = try {
        soapClient.simulerPensjon(
            request = request,
            tpOrdning = tpOrdning,
            tpLeverandor = tpLeverandor,
            tpOrdningStillingsprosentMap = stillingsprosentResponse.tpOrdningStillingsprosentMap
        ).let { addResponseInfoWhenSimulert(it, stillingsprosentResponse) }
    } catch (e: SimuleringException) {
        addResponseInfoWhenError(e)
    })

    private fun addResponseInfoWhenError(e: SimuleringException): List<SimulertPensjon> {
        metrics.incrementCounter(AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL)
        return listOf(SimulertPensjon(feilkode = e.feilkode, feilbeskrivelse = e.message, status = "FEIL"))
    }

    private fun addResponseInfoWhenSimulert(
        simulertPensjonList: List<SimulertPensjon>, stillingsprosentResponse: StillingsprosentResponse
    ): List<SimulertPensjon> {
        val utelatteTpNr = stillingsprosentResponse.exceptions.map(ExecutionException::cause)
            .filterIsInstance<StillingsprosentCallableException>().map(StillingsprosentCallableException::tpOrdning)
            .map(TPOrdning::tpId)
        val inkluderteTpNr = stillingsprosentResponse.tpOrdningStillingsprosentMap.keys.map(TPOrdning::tpId)
        return simulertPensjonList.onEach { simulertPensjon ->
            if (simulertPensjon.tpnr != null) {
                simulertPensjon.utelatteTpnr = utelatteTpNr
                simulertPensjon.inkluderteTpnr = inkluderteTpNr
                if (utelatteTpNr.isNotEmpty()) simulertPensjon.status = "UFUL"
            }
        }.also {
            incrementMetrics(it, utelatteTpNr)
        }
    }

    private fun incrementMetrics(simulertPensjonList: List<SimulertPensjon>, utelatteTpNr: List<String?>) {
        val ufullstendig = utelatteTpNr.isNotEmpty()
        val mangelfull = simulertPensjonList.flatMap { it.utbetalingsperioder ?: emptyList() }.any { it == null }

        if (ufullstendig) metrics.incrementCounter(
            AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
        )
        if (mangelfull) metrics.incrementCounter(
            AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
        )
        if (!ufullstendig && !mangelfull) metrics.incrementCounter(
            AppMetrics.Metrics.APP_NAME, AppMetrics.Metrics.APP_TOTAL_SIMULERING_OK
        )
    }

}
