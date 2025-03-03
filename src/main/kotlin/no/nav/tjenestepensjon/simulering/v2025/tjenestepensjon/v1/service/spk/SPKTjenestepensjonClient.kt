package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.sporingslogg.Organisasjon
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingsloggService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client.Companion.TJENESTE
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TpUtil
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.SPKSimulerTjenestepensjonResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class SPKTjenestepensjonClient(
    private val spkWebClient: WebClient,
    private val sporingsloggService: SporingsloggService,
    private val TpUtil: TpUtil
) : TjenestepensjonV2025Client, Pingable {
    private val log = KotlinLogging.logger {}

    override fun simuler(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjon> {
        val dto = SPKMapper.mapToRequest(request)
        log.debug { "Simulating tjenestepensjon 2025 hos SPK with request $dto" }
        sporingsloggService.loggUtgaaendeRequest(Organisasjon.SPK, request.pid, dto)
        try {
            val response = spkWebClient
                .post()
                .uri("$SIMULER_PATH/$tpNummer")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono<SPKSimulerTjenestepensjonResponse>()
                .block()
            return response?.let { Result.success(SPKMapper.mapToResponse(it, dto).also { res -> TpUtil.sammenlignOgLoggAfp(request, res.utbetalingsperioder) }) } ?: Result.failure(TjenestepensjonSimuleringException("No response body"))
        } catch (e: WebClientResponseException) {
            val errorMsg = "Failed to simulate tjenestepensjon 2025 hos SPK ${e.responseBodyAsString}"
            log.error(e) { errorMsg }
            return Result.failure(TjenestepensjonSimuleringException(errorMsg))
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to send request to simulate tjenestepensjon 2025 hos SPK med url ${e.uri}" }
            return Result.failure(TjenestepensjonSimuleringException("Failed to send request to simulate tjenestepensjon 2025 hos SPK"))
        }
    }

    override fun ping(): PingResponse {
        try {
            val response = spkWebClient.get()
                .uri(PING_PATH)
                .retrieve()
                .bodyToMono<String>()
                .block() ?: "PING OK, ingen response body"
            return PingResponse(PROVIDER, TJENESTE, response)
        } catch (e: WebClientResponseException) {
            val errorMsg = "Failed to ping SPK ${e.responseBodyAsString}"
            log.error(e) { errorMsg }
            return PingResponse(PROVIDER, TJENESTE, errorMsg)
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to ping SPK with url ${e.uri}" }
            return PingResponse(PROVIDER, TJENESTE, "Failed to ping to SPK")
        } catch (e: Exception) {
            log.error(e) { "An unexpected error occurred while pinging ${PROVIDER} ${e.message}" }
            return PingResponse(PROVIDER, TJENESTE, "Unexpected error: ${e.message}")
        }
    }

    companion object {
        const val SIMULER_PATH = "/nav/v2/tjenestepensjon/simuler"
        private const val PING_PATH = "/nav/admin/ping"
        private const val PROVIDER = "SPK"
    }
}