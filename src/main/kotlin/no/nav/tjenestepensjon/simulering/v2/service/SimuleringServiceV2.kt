package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.rest.SimuleringEndpoint
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.request.TpForhold
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class SimuleringServiceV2(
    private val restClient: RestClient, private val opptjeningsperiodeService: OpptjeningsperiodeService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun simulerOffentligTjenestepensjon(
        request: SimulerPensjonRequestV2,
        stillingsprosentResponse: StillingsprosentResponse,
        tpOrdning: TPOrdning,
        tpLeverandor: TpLeverandor
    ): SimulerOffentligTjenestepensjonResponse {
        val opptjeningsperiodeResponse = opptjeningsperiodeService.getOpptjeningsperiodeListe(stillingsprosentResponse)

        request.tpForholdListe = buildTpForhold(opptjeningsperiodeResponse.tpOrdningOpptjeningsperiodeMap)
        request.sisteTpnr = tpOrdning.tpId
        val requestWithFilteredFnr = SimuleringEndpoint.filterFnr(request.toString())
        log.debug("Populated request: $requestWithFilteredFnr")
        return try{
            restClient.getResponse(request = request, tpOrdning = tpOrdning, tpLeverandor = tpLeverandor)
        }
        catch (e: WebClientResponseException){
            log.error("Error <${e.responseBodyAsString}> while calling ${tpLeverandor.name} with request: $requestWithFilteredFnr", e)
            throw e
        }
    }

    private fun buildTpForhold(tpOrdningOpptjeningsperiodeMap: TPOrdningOpptjeningsperiodeMap) =
        tpOrdningOpptjeningsperiodeMap.map { entry -> TpForhold(entry.key.tpId, entry.value) }

}
