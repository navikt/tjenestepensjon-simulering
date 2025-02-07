package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.sporingslogg.Organisasjon
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingsloggService
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class RestClient(
    private val restGatewayWebClient: WebClient,
    private val spkWebClient: WebClient,
    @Value("\${oftp.before2025.spk.maskinportenscope}") private val scope: String,
    private val sporingsloggService: SporingsloggService,
) {

    fun getResponse(request: SimulerPensjonRequestV2, tpOrdning: TPOrdningIdDto): SimulerOffentligTjenestepensjonResponse {
        sporingsloggService.loggUtgaaendeRequest(Organisasjon.SPK, request.fnr.fnr, request)
        val response: SimulerOffentligTjenestepensjonResponse? = restGatewayWebClient
            .post()
            .uri("/medlem/pensjon/prognose/v1")
            .header("scope", scope)
            .bodyValue(request)
            .retrieve()
            .bodyToMono<SimulerOffentligTjenestepensjonResponse>()
            .block()
        return response
            ?: SimulerOffentligTjenestepensjonResponse(request.sisteTpnr, tpOrdning.tpId)
    }

    fun ping(): String = spkWebClient
        .post()
        .uri("/nav/pensjon/prognose/v1")
        .header("scope", scope)
        .bodyValue(dummyRequest())
        .retrieve()
        .bodyToMono(String::class.java)
        .block() ?: "No body received"

    private fun dummyRequest(fnr: String = "01015512345") = SimulerPensjonRequestV2(
        fnr = FNR(fnr),
        fodselsdato = "01-01-1955",
        sisteTpnr = "3010",
        sivilstandkode = SivilstandCodeEnum.UGIF,
        inntektListe = emptyList(),
        simuleringsperiodeListe = emptyList(),
        simuleringsdataListe = emptyList()
    )
}
