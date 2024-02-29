package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.Delingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.HentDelingstallRequest
import no.nav.tjenestepensjon.simulering.model.domain.pen.HentDelingstallResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class PenClient(val penWebClient: WebClient) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun hentDelingstall(aarskull: Int, alder: List<Alder>): List<Delingstall> {
        return try {
            penWebClient.post()
                .uri("/delingstall")
                .bodyValue(HentDelingstallRequest(aarskull, alder))
                .retrieve()
                .bodyToMono(HentDelingstallResponse::class.java)
                .block()!!
                .delingstall
        } catch (e: WebClientRequestException) {
            log.error("Request to get delingstall failed: " + e.message, e)
            throw RuntimeException("Noe gikk galt ved henting av delingstall fra PEN")
        } catch (e: WebClientResponseException) {
            log.error("Request to get delingstall failed with response: " + e.responseBodyAsString, e)
            throw RuntimeException("Noe gikk galt ved henting av delingstall fra PEN")
        }
    }
}