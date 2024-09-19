package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class SPKTjenestepensjonClient(private val spkWebClient: WebClient) : TjenestepensjonV2025Client {
    private val log = KotlinLogging.logger {}

    override fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjon> {
        try {
            val response = spkWebClient
                .post()
                .uri("/nav/v2/tjenestepensjon/simuler/3010")
                .bodyValue(SPKMapper.mapToRequest(request))
                .retrieve()
                .bodyToMono<SPKSimulerTjenestepensjonResponse>()
                .block()
            return response?.let { Result.success(SPKMapper.mapToResponse(it)) } ?: Result.failure(TjenestepensjonSimuleringException("No response body"))
        } catch (e: WebClientResponseException) {
            val errorMsg = "Failed to simulate tjenestepensjon 2025 hos SPK ${ e.responseBodyAsString}"
            log.error(e) { errorMsg }
            return Result.failure(TjenestepensjonSimuleringException(errorMsg))
        } catch (e: WebClientRequestException){
            log.error(e) { "Failed to send request to simulate tjenestepensjon 2025 hos SPK med url ${e.uri}" }
            return Result.failure(TjenestepensjonSimuleringException("Failed to send request to simulate tjenestepensjon 2025 hos SPK"))
        }
    }
}