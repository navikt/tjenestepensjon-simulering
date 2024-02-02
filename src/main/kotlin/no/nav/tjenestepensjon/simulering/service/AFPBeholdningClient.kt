package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class AFPBeholdningClient(val afpBeholdningWebClient: WebClient) {
    private val log = LoggerFactory.getLogger(javaClass)

        fun simulerAFPBeholdningGrunnlag(simulerAFPBeholdningGrunnlagRequest: SimulerAFPBeholdningGrunnlagRequest): SimulerAFPBeholdningGrunnlagResponse {
            return try {
                afpBeholdningWebClient.post()
                    .uri("/api/simuler")
                    .bodyValue(simulerAFPBeholdningGrunnlagRequest)
                    .retrieve()
                    .toEntity(SimulerAFPBeholdningGrunnlagResponse::class.java)
                    .block()?.body!!
            } catch (e: WebClientRequestException){
                log.error("Request to get AFP Beholdninger failed: "  + e.message, e)
                throw RuntimeException("Noe gikk galt ved henting av AFP beholdninger")
            }
            catch (e: WebClientResponseException) {
                log.error("Request to get AFP Beholdninger failed with response: "  + e.responseBodyAsString, e)
                throw RuntimeException("Noe gikk galt ved henting av AFP beholdninger")
            }
        }
}