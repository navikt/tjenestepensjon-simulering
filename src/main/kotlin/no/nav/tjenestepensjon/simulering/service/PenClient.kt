package no.nav.tjenestepensjon.simulering.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.Delingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.HentDelingstallRequest
import no.nav.tjenestepensjon.simulering.model.domain.pen.HentDelingstallResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry

@Service
class PenClient(val penWebClient: WebClient) {
    private val log = KotlinLogging.logger {}
    fun hentDelingstall(aarskull: Int, alder: List<Alder>): List<Delingstall> {
        return try {
            penWebClient.post()
                .uri("/delingstall")
                .bodyValue(HentDelingstallRequest(aarskull, alder))
                .retrieve()
                .bodyToMono(HentDelingstallResponse::class.java)
                .retryWhen(
                    Retry.backoff(3, java.time.Duration.ofSeconds(1))
                        .maxBackoff(java.time.Duration.ofSeconds(10))
                        .jitter(0.2) // 20% tilfeldig forsinkelse
                        .doBeforeRetry { retrySignal ->
                            log.warn { "Retrying henting av delingstall due to: ${retrySignal.failure().message}, attempt: ${retrySignal.totalRetries() + 1}" }
                        }.onRetryExhaustedThrow({ _, _ -> RuntimeException("Failed to get delingstall after all retries") })
                )
                .block()!!
                .delingstall
        } catch (e: WebClientRequestException) {
            val errorMessage = "Request to get delingstall failed: ${e.message}"
            log.error(e) { errorMessage }
            throw RuntimeException(errorMessage)
        } catch (e: WebClientResponseException) {
            val errorMessage = "Request to get delingstall failed with response: ${e.responseBodyAsString}"
            log.error(e) { errorMessage }
            throw RuntimeException(errorMessage)
        }
    }
}