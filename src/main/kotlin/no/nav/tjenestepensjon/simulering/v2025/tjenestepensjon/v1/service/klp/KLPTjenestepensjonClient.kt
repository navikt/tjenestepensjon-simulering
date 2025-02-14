package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

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
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.InkludertOrdning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.KLPSimulerTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.Utbetaling
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class KLPTjenestepensjonClient(
    private val klpWebClient: WebClient,
    private val sporingsloggService: SporingsloggService,
    @Value("\${spring.profiles.active:}") private val activeProfiles: String,
) : TjenestepensjonV2025Client, Pingable {
    private val log = KotlinLogging.logger {}

    override fun simuler(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjon> {
        val response = if (activeProfiles.contains("dev-gcp")) {
            provideMockResponse(request)
        } else {
            val dto = KLPMapper.mapToRequest(request)
            sporingsloggService.loggUtgaaendeRequest(Organisasjon.KLP, request.pid, dto)

            try {
                klpWebClient
                    .post()
                    .uri("$SIMULER_PATH/$tpNummer")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono<KLPSimulerTjenestepensjonResponse>()
                    .block()
            } catch (e: WebClientResponseException) {
                val errorMsg = "Failed to simulate tjenestepensjon 2025 hos KLP ${e.responseBodyAsString}"
                log.error(e) { errorMsg }
                return Result.failure(TjenestepensjonSimuleringException(errorMsg))
            } catch (e: WebClientRequestException) {
                log.error(e) { "Failed to send request to simulate tjenestepensjon 2025 hos KLP med url ${e.uri}" }
                return Result.failure(TjenestepensjonSimuleringException("Failed to send request to simulate tjenestepensjon 2025 hos KLP"))
            }
        }
        return response?.let { Result.success(KLPMapper.mapToResponse(it, KLPMapper.mapToLoggableRequestDto(request))) } ?: Result.failure(TjenestepensjonSimuleringException("No response body"))
    }

    override fun ping(): PingResponse {
        return PingResponse(PROVIDER, TJENESTE, "Støttes ikke")
    }


    companion object {
        const val SIMULER_PATH = "/api/oftp/simulering"
        private const val PING_PATH = ""
        private const val PROVIDER = "KLP"

        fun provideMockResponse(request: SimulerTjenestepensjonRequestDto): KLPSimulerTjenestepensjonResponse {
            return KLPSimulerTjenestepensjonResponse(
                inkludertOrdningListe = listOf(InkludertOrdning("3100")),
                utbetalingsListe = listOf(
                    Utbetaling(fraOgMedDato = request.uttaksdato, manedligUtbetaling = 3576, arligUtbetaling = 42914, ytelseType = "PAASLAG"),
                    Utbetaling(fraOgMedDato = request.uttaksdato.plusYears(5), manedligUtbetaling = 2232, arligUtbetaling = 26779, ytelseType = "APOF2020"),
                    Utbetaling(fraOgMedDato = request.uttaksdato, manedligUtbetaling = 884, arligUtbetaling = 10609, ytelseType = "BTP"),
                ),
                arsakIngenUtbetaling = emptyList(),
                betingetTjenestepensjonErInkludert = false,
            )
        }
    }
}