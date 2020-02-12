package no.nav.tjenestepensjon.simulering.rest

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.TPOrdningStillingsprosentMap
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component

@Component
class RestClient(private val tokenClient: TokenClient) : Tjenestepensjonsimulering {
    private val webClient = WebClientConfig.webClient()
    override fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ): List<Stillingsprosent> =
            webClient.get()
                    .uri(tpLeverandor.url)
                    .header(AUTHORIZATION, "Bearer " + tokenClient.oidcAccessToken)
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
                    .header(AUTHORIZATION, "Bearer " + tokenClient.oidcAccessToken)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<List<SimulertPensjon>>() {})
                    .block() ?: emptyList()

}