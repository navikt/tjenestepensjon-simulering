package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class KLPTjenestepensjonClient(private val webClient: WebClient) : TjenestepensjonV2025Client {
    private val log = KotlinLogging.logger {}

    override fun simuler(request: SimulerTjenestepensjonRequestDto): SimulertTjenestepensjon {
        try {
            val response = webClient
                .post()
                .uri("/api/oftp/simulering/3200")
                .bodyValue(KLPMapper.mapToRequest(request))
                .retrieve()
                .bodyToMono<KLPSimulerTjenestepensjonResponse>()
                .block()
            return response?.let { KLPMapper.mapToResponse(it) } ?: throw TjenestepensjonSimuleringException("No response body")
        } catch (e: WebClientResponseException) {
            val errorMsg = e.responseBodyAsString
            log.error(e) { "Failed to simulate tjenestepensjon 2025 hos KLP ${errorMsg}" }
            if (e.statusCode.is4xxClientError) {
                throw RuntimeException("Failed to simulate tjenestepensjon 2025 hos KLP ${errorMsg}", e)
            }
            throw TjenestepensjonSimuleringException(errorMsg)
        }
    }
}