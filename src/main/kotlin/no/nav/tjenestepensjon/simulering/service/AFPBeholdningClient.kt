package no.nav.tjenestepensjon.simulering.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.popp.AFPGrunnlagBeholdningPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

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
                    .block()?.body!!
                    .pensjonsBeholdningsPeriodeListe
            } catch (e: WebClientRequestException) {
                log.error(e) { "${"Request to get AFP Beholdninger failed: " + e.message}" }
                throw RuntimeException("Noe gikk galt ved henting av AFP beholdninger")
            } catch (e: WebClientResponseException) {
                log.error(e) { "${"Request to get AFP Beholdninger failed with response: " + e.responseBodyAsString}" }
                throw RuntimeException("Noe gikk galt ved henting av AFP beholdninger")
            }
        }
}