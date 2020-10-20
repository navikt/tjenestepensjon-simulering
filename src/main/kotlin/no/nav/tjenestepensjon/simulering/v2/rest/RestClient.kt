package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class RestClient(val webClient: WebClient) {
    @Autowired
    private lateinit var tokenClient: TokenClient

    @Value("\${PEPROXY_HTTPHEADERS_TARGET_AUTHORIZATION}")
    lateinit var peproxyHttpheadersTargetAuthorization: String

    @Value("\${PEPROXY_HTTPHEADERS_TARGET_URL}")
    lateinit var peproxyHttpheadersTargetUrl: String

    @Value("\${PEPROXY_URL}")
    lateinit var peproxyUrl: String

    fun getResponse(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ): SimulerOffentligTjenestepensjonResponse =
            webClient.post()
                    .uri(peproxyUrl)
                    .header(peproxyHttpheadersTargetUrl, tpLeverandor.simuleringUrl)
                    .header("x-application-id", "NAV")
                    .header(peproxyHttpheadersTargetAuthorization, "Bearer " +
                            if (tpLeverandor.name != "SPK") tokenClient.pensjonsimuleringToken
                            else tokenClient.oidcAccessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono<SimulerOffentligTjenestepensjonResponse>()
                    .block() ?: SimulerOffentligTjenestepensjonResponse(
                    tpnr = request.sisteTpnr,
                    navnOrdning = tpOrdning.tpId
            )
}