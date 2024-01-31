package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAfpBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAfpBeholdningGrunnlagResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AFPBeholdningClient(val afpBeholdningWebClient: WebClient) {
    private val log = LoggerFactory.getLogger(javaClass)

        fun simulerAfpBeholdningGrunnlag(simulerAfpBeholdningGrunnlagRequest: SimulerAfpBeholdningGrunnlagRequest): SimulerAfpBeholdningGrunnlagResponse? {
            return try {
                afpBeholdningWebClient.post()
                    .uri("/api/simuler")
                    .bodyValue(simulerAfpBeholdningGrunnlagRequest)
                    .retrieve()
                    .toEntity(SimulerAfpBeholdningGrunnlagResponse::class.java)
                    .block()?.body
            } catch (e: Throwable) {
                log.error("Request to get AFP Beholdninger failed: "  + e.message, e)
                return null
            }
        }
}