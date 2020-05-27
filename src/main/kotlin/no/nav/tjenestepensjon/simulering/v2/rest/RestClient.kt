package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service

@Service
class RestClient {
    @Autowired
    private lateinit var tokenClient: TokenClient

    private val webClient = WebClientConfig.webClient()

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
                    .header(peproxyHttpheadersTargetAuthorization, "Bearer " + if (!tpLeverandor.name.equals("SPK")) tokenClient.maskinportToken else tokenClient.oidcAccessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<SimulerOffentligTjenestepensjonResponse>() {})
                    .block() ?: SimulerOffentligTjenestepensjonResponse(
                    tpnr = request.sisteTpnr,
                    navnOrdning = tpOrdning.tpId
            )
}