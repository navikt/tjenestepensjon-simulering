package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

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
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPMapper.mapToRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPMapper.mapToResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.InkludertOrdning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.KLPSimulerTjenestepensjonRequest
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
    private val sporingslogg: SporingsloggService,
    @param:Value("\${spring.profiles.active:}") private val activeProfiles: String,
    private val sammenligner: SammenlignAFPService,
) : TjenestepensjonV2025Client, Pingable {
    private val log = KotlinLogging.logger {}

    override fun simuler(spec: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjon> {
        val request: KLPSimulerTjenestepensjonRequest = mapToRequest(spec)
        val response = if (activeProfiles.contains("dev-gcp")) {
            provideMockResponse(spec)
        } else {
            sporingslogg.loggUtgaaendeRequest(Organisasjon.KLP, spec.pid, request)

            try {
                klpWebClient
                    .post()
                    .uri("$SIMULER_PATH/$tpNummer")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono<KLPSimulerTjenestepensjonResponse>()
                    .block()
            } catch (e: WebClientResponseException) {
                "Failed to simulate tjenestepensjon 2025 hos $PROVIDER ${e.responseBodyAsString}".let {
                    log.error(e) { it }
                    return Result.failure(TjenestepensjonSimuleringException(it))
                }
            } catch (e: WebClientRequestException) {
                "Failed to send request to simulate tjenestepensjon 2025 hos $PROVIDER".let {
                    log.error(e) { "$it med url ${e.uri}" }
                    return Result.failure(TjenestepensjonSimuleringException(it))
                }
            }
        }
        return response?.let { success(request, spec, it) }
            ?: Result.failure(TjenestepensjonSimuleringException("No response body"))
    }

    override fun ping() =
        PingResponse(provider = PROVIDER, tjeneste = TJENESTE, melding = "Støttes ikke")

    private fun success(
        request: KLPSimulerTjenestepensjonRequest,
        spec: SimulerTjenestepensjonRequestDto,
        response: KLPSimulerTjenestepensjonResponse
    ): Result<SimulertTjenestepensjon> =
        Result.success(
            mapToResponse(response, request)
                .also { sammenligner.sammenlignOgLoggAfp(spec, it.utbetalingsperioder) })

    companion object {
        private const val PROVIDER = "KLP"
        private const val SIMULER_PATH = "/api/oftp/simulering"

        fun provideMockResponse(spec: SimulerTjenestepensjonRequestDto) =
            KLPSimulerTjenestepensjonResponse(
                inkludertOrdningListe = listOf(InkludertOrdning("3100")),
                utbetalingsListe = listOf(
                    Utbetaling(fraOgMedDato = spec.uttaksdato, manedligUtbetaling = 3576, arligUtbetaling = 42914, ytelseType = "PAASLAG"),
                    Utbetaling(fraOgMedDato = spec.uttaksdato.plusYears(5), manedligUtbetaling = 2232, arligUtbetaling = 26779, ytelseType = "APOF2020"),
                    Utbetaling(fraOgMedDato = spec.uttaksdato, manedligUtbetaling = 884, arligUtbetaling = 10609, ytelseType = "BTP"),
                ),
                arsakIngenUtbetaling = emptyList(),
                betingetTjenestepensjonErInkludert = false,
            )
    }
}
