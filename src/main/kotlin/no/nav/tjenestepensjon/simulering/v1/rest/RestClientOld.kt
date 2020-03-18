package no.nav.tjenestepensjon.simulering.v1.rest

import no.nav.tjenestepensjon.simulering.v1.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.v1.consumer.TokenClientOld
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component

@Component
class RestClientOld(private val tokenClientOld: TokenClientOld) : Tjenestepensjonsimulering {
    private val webClient = WebClientConfig.webClient()
    override fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ): List<Stillingsprosent> =
            webClient.get()
                    .uri(tpLeverandor.url)
                    .header(AUTHORIZATION, "Bearer " + tokenClientOld.oidcAccessToken)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<List<Stillingsprosent>>() {})
                    .block() ?: emptyList()

    override fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ): List<SimulertPensjon> =
            webClient.get()
                    .uri(tpLeverandor.url)
                    .header(AUTHORIZATION, "Bearer " + tokenClientOld.oidcAccessToken)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<List<SimulertPensjon>>() {})
                    .block() ?: emptyList()

}