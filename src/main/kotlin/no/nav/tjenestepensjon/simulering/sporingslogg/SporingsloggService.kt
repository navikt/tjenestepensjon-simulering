package no.nav.tjenestepensjon.simulering.sporingslogg

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingDtoMapper.toDto
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import java.time.Duration

@Service
class SporingsloggService(val sporingsloggGatewayWebClient: WebClient, val objectMapper: ObjectMapper, private val environment: Environment) {
    private val log = KotlinLogging.logger {}

    fun loggUtgaaendeRequest(organisasjon: Organisasjon, ident: String, data: Any) {
        if (environment.activeProfiles.contains("prod-gcp")) {
            try {
                val dataSendtIRequest = objectMapper.writeValueAsString(data)
                rapporter(Sporingsrapport(ident, organisasjon.organisasjonsnummer, dataSendtIRequest))
            } catch (e: JsonProcessingException) {
                log.error(e) { "Failed to serialize data to JSON from class: ${data.javaClass}" }
            } catch (e: Exception) {
                log.error(e) { "Failed to send sporingsrapport" }
            }
        }
    }

    private fun rapporter(sporingsrapport: Sporingsrapport) {
        sporingsloggGatewayWebClient.post()
            .uri("/sporingslogg/api/post")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(toDto(sporingsrapport))
            .retrieve()
            .toBodilessEntity()
            .retryWhen(RETRY)
            .doOnSuccess {
                log.info { "Successfully sent sporingsrapport" }
            }
            .doOnError { e ->
                when (e) {
                    is WebClientRequestException -> log.error(e) { "Request to sporingslogg failed at ${e.uri}" }
                    is WebClientResponseException -> log.error(e) { "Request to sporingslogg failed with status code: ${e.statusCode}" }
                    else -> log.error(e) { "Request to sporingslogg failed" }
                }
            }
            .subscribe() // Fire-and-forget
    }

    companion object {
        private val RETRY = Retry.backoff(3, Duration.ofSeconds(1))
    }
}
