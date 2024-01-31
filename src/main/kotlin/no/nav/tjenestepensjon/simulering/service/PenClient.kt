package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.Delingstall
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class PenClient(val penWebClient: WebClient) {

    fun hentDelingstall(arskull: Int, alder: Alder) : Delingstall = penWebClient.post()
        .uri("/delingstall")
        .bodyValue(object {val arskull = arskull; val alder = alder})
        .retrieve()
        .toEntity(Delingstall::class.java)
        .block()?.body ?: throw RuntimeException("Noe gikk galt ved henting av delingstall fra PEN")
}