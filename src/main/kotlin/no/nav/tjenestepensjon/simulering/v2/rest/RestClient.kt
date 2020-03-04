package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component

@Component
class RestClient(private val tokenClient: TokenClient) {
    private val webClient = WebClientConfig.webClient()
    fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ): List<Opptjeningsperiode> =
            webClient.get()
                    .uri(tpLeverandor.url)
                    .header(AUTHORIZATION, "Bearer " + tokenClient.oidcAccessToken)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<List<Opptjeningsperiode>>() {})
                    .block() ?: emptyList()

    fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ): SimulerOffentligTjenestepensjonResponse =
            webClient.get()
                    .uri(tpLeverandor.url)
                    .header(AUTHORIZATION, "Bearer " + tokenClient.oidcAccessToken)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<SimulerOffentligTjenestepensjonResponse>() {})
                    .block() ?: SimulerOffentligTjenestepensjonResponse(
                        tpnr = request.sisteTpnr,
                        navnOrdning = tpOrdning.tpId,
                        inkluderteOrdningeListe = null,
                        leverandorUrl = null,
                        utbetalingsperiodeListe = null
                    )
}