package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.Delingstall
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class PenClient(val penWebClient: WebClient) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun hentDelingstall(arskull: Int, alder: Alder): Delingstall {
        return try {
            penWebClient.post()
                .uri("/delingstall")
                .bodyValue(object {
                    val arskull = arskull;
                    val alder = alder
                })
                .retrieve()
                .toEntity(Delingstall::class.java)
                .block()?.body!!
        } catch (e: WebClientRequestException) {
            log.error("Request to get AFP Beholdninger failed: " + e.message, e)
            throw RuntimeException("Noe gikk galt ved henting av delingstall fra PEN")
        } catch (e: WebClientResponseException) {
            log.error("Request to get AFP Beholdninger failed with response: " + e.responseBodyAsString, e)
            throw RuntimeException("Noe gikk galt ved henting av delingstall fra PEN")
        }
    }
}