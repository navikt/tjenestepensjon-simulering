package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class RestClient(
    private val webClient: WebClient,
    private val maskinportenTokenClient: MaskinportenTokenClient,
    private @Value("\${oftp.before2025.spk.maskinportenscope}") val scope: String,
) {

    fun getResponse(
        request: SimulerPensjonRequestV2, tpOrdning: TPOrdningIdDto, tpLeverandor: TpLeverandor
    ): SimulerOffentligTjenestepensjonResponse = webClient.post().uri(tpLeverandor.simuleringUrl).headers {
        it.setBearerAuth(
            maskinportenTokenClient.pensjonsimuleringToken(scope)
        )
    }.bodyValue(request).retrieve().bodyToMono<SimulerOffentligTjenestepensjonResponse>().block()
        ?: SimulerOffentligTjenestepensjonResponse(request.sisteTpnr, tpOrdning.tpId)
}
