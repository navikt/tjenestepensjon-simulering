package no.nav.tjenestepensjon.simulering.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.popp.AFPGrunnlagBeholdningPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry

@Service
class AFPBeholdningClient(val afpBeholdningWebClient: WebClient) {
    private val log = KotlinLogging.logger {}

    fun simulerAFPBeholdningGrunnlag(simulerAFPBeholdningGrunnlagRequest: SimulerAFPBeholdningGrunnlagRequest): List<AFPGrunnlagBeholdningPeriode> {
        return try {
            afpBeholdningWebClient.post()
                .uri("/api/simuler")
                .bodyValue(simulerAFPBeholdningGrunnlagRequest)
                .retrieve()
                .toEntity(SimulerAFPBeholdningGrunnlagResponse::class.java)
                .retryWhen(
                    Retry.backoff(4, java.time.Duration.ofSeconds(2))
                        .maxBackoff(java.time.Duration.ofSeconds(10))
                        .jitter(0.3) // 30% tilfeldig forsinkelse
                        .doBeforeRetry { retrySignal ->
                            log.warn { "Retrying henting av AFP Beholdninger due to: ${retrySignal.failure().message}, attempt: ${retrySignal.totalRetries() + 1}" }
                        }.onRetryExhaustedThrow({ _, _ -> RuntimeException("Failed to get AFP Beholdninger after all retries") })
                )
                .block()?.body!!
                .pensjonsBeholdningsPeriodeListe
        } catch (e: WebClientRequestException) {
            val errorMessage = "Request to get AFP Beholdninger failed: " + e.message
            log.error(e) { errorMessage }
            throw RuntimeException(errorMessage)
        } catch (e: WebClientResponseException) {
            val errorMessage = "Request to get AFP Beholdninger failed with response: " + e.responseBodyAsString
            log.error(e) { errorMessage }
            throw RuntimeException(errorMessage)
        }
    }
}