package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.ping.Pingable
import no.nav.tjenestepensjon.simulering.sporingslogg.Organisasjon
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingsloggService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.SammenlignAFPService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client.Companion.TJENESTE
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKMapper.mapToRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKMapper.mapToResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.SPKSimulerTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.SPKSimulerTjenestepensjonResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class SPKTjenestepensjonClient(
    private val spkWebClient: WebClient,
    private val sporingslogg: SporingsloggService,
    private val sammenligner: SammenlignAFPService
) : TjenestepensjonV2025Client, Pingable {
    private val log = KotlinLogging.logger {}

    override fun simuler(spec: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjon> {
        val request: SPKSimulerTjenestepensjonRequest = mapToRequest(spec)
        log.debug { "Simulating tjenestepensjon 2025 with $PROVIDER with request $request" }
        sporingslogg.loggUtgaaendeRequest(Organisasjon.SPK, spec.pid, request)

        return try {
            spkWebClient
                .post()
                .uri("$SIMULER_PATH/$tpNummer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono<SPKSimulerTjenestepensjonResponse>()
                .block()
                ?.let { success(spec, request, response = it) }
                ?: Result.failure(TjenestepensjonSimuleringException("No response body"))
        } catch (e: WebClientResponseException) {
            "Failed to simulate tjenestepensjon 2025 with $PROVIDER ${e.responseBodyAsString}".let {
                log.error(e) { it }
                Result.failure(TjenestepensjonSimuleringException(it))
            }
        } catch (e: WebClientRequestException) {
            "Failed to send request to simulate tjenestepensjon 2025 with $PROVIDER".let {
                log.error(e) { "$it med url ${e.uri}" }
                Result.failure(TjenestepensjonSimuleringException(it))
            }
        }
    }

    private fun success(
        spec: SimulerTjenestepensjonRequestDto,
        request: SPKSimulerTjenestepensjonRequest,
        response: SPKSimulerTjenestepensjonResponse
    ): Result<SimulertTjenestepensjon> =
        Result.success(
            mapToResponse(response, request)
                .also { sammenligner.sammenlignOgLoggAfp(spec, it.utbetalingsperioder) }
        )

    override fun ping(): PingResponse {
        return try {
            val response = spkWebClient.get()
                .uri(PING_PATH)
                .retrieve()
                .bodyToMono<String>()
                .block() ?: "PING OK, ingen response body"
            pingResponse(response)
        } catch (e: WebClientResponseException) {
            "Failed to ping $PROVIDER ${e.responseBodyAsString}".let {
                log.error(e) { it }
                pingResponse(it)
            }
        } catch (e: WebClientRequestException) {
            "Failed to ping to $PROVIDER".let {
                log.error(e) { "$it with url ${e.uri}" }
                pingResponse(it)
            }
        } catch (e: Exception) {
            log.error(e) { "An unexpected error occurred while pinging $PROVIDER ${e.message}" }
            pingResponse("Unexpected error: ${e.message}")
        }
    }

    companion object {
        private const val PROVIDER = "SPK"
        private const val SIMULER_PATH = "/nav/v2/tjenestepensjon/simuler"
        private const val PING_PATH = "/nav/admin/ping"

        private fun pingResponse(message: String) =
            PingResponse(provider = PROVIDER, tjeneste = TJENESTE, melding = message)
    }
}
