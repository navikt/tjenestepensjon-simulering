package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.exceptions.OpptjeningsperiodeCallableException
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.request.TpForhold
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class SimpleSimuleringService(
        private val restClient: RestClient,
        private val opptjeningsperiodeService: OpptjeningsperiodeService,
        private val metrics: AppMetrics
) : SimuleringService {
    override fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest,
                                                 stillingsprosentResponse: StillingsprosentResponse,
                                                 tpOrdning: TPOrdning,
                                                 tpLeverandor: TpLeverandor): SimulerOffentligTjenestepensjonResponse {
        val opptjeningsperiodeResponse = opptjeningsperiodeService.getOpptjeningsperiodeListe(stillingsprosentResponse)

        request.tpForholdListe = buildTpForhold(opptjeningsperiodeResponse.tpOrdningOpptjeningsperiodeMap)
        request.sisteTpnr = tpOrdning.tpId

        return restClient.getResponse(request = request, tpOrdning = tpOrdning, tpLeverandor = tpLeverandor)
                .also { addResponseInfoWhenSimulert(it, opptjeningsperiodeResponse) }
    }

    private fun buildTpForhold(tpOrdningOpptjeningsperiodeMap: TPOrdningOpptjeningsperiodeMap) = tpOrdningOpptjeningsperiodeMap
        .map{ entry ->
            TpForhold(
                    entry.key.tpId,
                    entry.value
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

        //val ufullstendig = utelatteTpNr.isNotEmpty()
        //val mangelfull = response.utbetalingsperiodeListe.isNullOrEmpty()
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}