package no.nav.tjenestepensjon.simulering.v2.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.exceptions.BrukerKvalifisererIkkeTilTjenestepensjonException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.rest.SimuleringEndpoint
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v2.TPOrdningOpptjeningsperiodeMap
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.request.TpForhold
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.SPKTjenestepensjonClientPre2025
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class SPKTjenestepensjonServicePre2025(
    private val SPKTjenestepensjonClientPre2025: SPKTjenestepensjonClientPre2025, private val opptjeningsperiodeService: OpptjeningsperiodeService
) {
    private val log = KotlinLogging.logger {}

    fun simulerOffentligTjenestepensjon(
        request: SimulerPensjonRequestV2,
        stillingsprosentResponse: StillingsprosentResponse,
        tpOrdning: TPOrdningIdDto,
        tpLeverandor: TpLeverandor
    ): SimulerOffentligTjenestepensjonResponse {
        val opptjeningsperiodeResponse = opptjeningsperiodeService.getOpptjeningsperiodeListe(stillingsprosentResponse)

        request.tpForholdListe = buildTpForhold(opptjeningsperiodeResponse.tpOrdningOpptjeningsperiodeMap)
        request.sisteTpnr = tpOrdning.tpId
        val requestWithFilteredFnr = SimuleringEndpoint.filterFnr(request.toString())
        log.debug { "Populated request: $requestWithFilteredFnr" }
        return try {
            SPKTjenestepensjonClientPre2025.getResponse(request = request, tpOrdning = tpOrdning)
        } catch (e: WebClientResponseException) {
            val responseBody = e.responseBodyAsString.let { StringUtils.replace(it, "Ã¥", "å") }
                .let { StringUtils.replace(it, "Ã\u0083Â¥", "å") }
                .let { StringUtils.replace(it, "Ã¦", "æ") }
                .let { StringUtils.replace(it, "Ã¸", "ø") }
                .let { StringUtils.replace(it, "Ã\u0083Â¸", "ø") }

            log.error(e) { "Error <$responseBody> while calling ${tpLeverandor.name} with request: $requestWithFilteredFnr" }
            if (responseBody.contains("Validation problem")) {
                throw BrukerKvalifisererIkkeTilTjenestepensjonException(responseBody)
            }
            throw e
        }
    }

    fun ping() : String = SPKTjenestepensjonClientPre2025.ping()

    private fun buildTpForhold(tpOrdningOpptjeningsperiodeMap: TPOrdningOpptjeningsperiodeMap) =
        tpOrdningOpptjeningsperiodeMap.map { entry -> TpForhold(entry.key.tpId, entry.value) }

}
