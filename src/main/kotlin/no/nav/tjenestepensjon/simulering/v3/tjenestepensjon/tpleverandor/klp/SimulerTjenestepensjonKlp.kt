package no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.tpleverandor.klp

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.domain.SisteOrdning
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class SimulerTjenestepensjonKlp(
    private val webClient: WebClient
) {
    @Autowired
    private lateinit var tokenClient: TokenClient

    fun simuler(request: SimulerTjenestepensjonRequestKlp, sisteOrdning: SisteOrdning) : SimulerTjenestepensjonResponseKlp =
        webClient.post().uri(sisteOrdning.simuleringsUrl).headers {
            it.setBearerAuth(tokenClient.pensjonsimuleringToken())
        }.bodyValue(request).retrieve().bodyToMono<SimulerTjenestepensjonResponseKlp>().block()
            ?: SimulerTjenestepensjonResponseKlp(sisteOrdning.tpnr)
    }