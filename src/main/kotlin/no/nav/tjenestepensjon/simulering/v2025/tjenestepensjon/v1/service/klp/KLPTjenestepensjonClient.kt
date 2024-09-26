package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client.Companion.TJENESTE
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class KLPTjenestepensjonClient(private val klpWebClient: WebClient) : TjenestepensjonV2025Client, Pingable {
    private val log = KotlinLogging.logger {}

    override fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjon> {
        try {
            val response = klpWebClient
                .post()
                .uri(SIMULER_PATH)
                .bodyValue(KLPMapper.mapToRequest(request))
                .retrieve()
                .bodyToMono<KLPSimulerTjenestepensjonResponse>()
                .block()
            return response?.let { Result.success(KLPMapper.mapToResponse(it)) } ?: Result.failure(TjenestepensjonSimuleringException("No response body"))
        } catch (e: WebClientResponseException) {
            val errorMsg = "Failed to simulate tjenestepensjon 2025 hos KLP ${ e.responseBodyAsString}"
            log.error(e) { errorMsg }
            return Result.failure(TjenestepensjonSimuleringException(errorMsg))
        } catch (e: WebClientRequestException){
            log.error(e) { "Failed to send request to simulate tjenestepensjon 2025 hos KLP med url ${e.uri}" }
            return Result.failure(TjenestepensjonSimuleringException("Failed to send request to simulate tjenestepensjon 2025 hos KLP"))
        }
    }

    override fun ping(): PingResponse {
        return PingResponse(PROVIDER, TJENESTE, "Støttes ikke")
    }

    companion object {
        private const val SIMULER_PATH = "/api/oftp/simulering/3200"
        private const val PING_PATH = ""
        private const val PROVIDER = "KLP"
    }
}