package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class RestClient(private val webClient: WebClient) {
    @Autowired
    private lateinit var tokenClient: TokenClient

    fun getResponse(
        request: SimulerPensjonRequestV2, tpOrdning: TPOrdningIdDto, tpLeverandor: TpLeverandor
    ): SimulerOffentligTjenestepensjonResponse = webClient.post().uri(tpLeverandor.simuleringUrl).headers {
        it.setBearerAuth(
            if (tpLeverandor.name != "SPK") tokenClient.pensjonsimuleringToken()
            else tokenClient.oidcAccessToken.accessToken
        )
    }.bodyValue(request).retrieve().bodyToMono<SimulerOffentligTjenestepensjonResponse>().block()
        ?: SimulerOffentligTjenestepensjonResponse(request.sisteTpnr, tpOrdning.tpId)
}
