package no.nav.tjenestepensjon.simulering.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.Tjenestepensjon2025Aggregator.aggregerRespons
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.ResultatTypeDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimulerTjenestepensjonResponseDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TjenestepensjonSimuleringV2025Controller(
    private val tjenestepensjonV2025Service: TjenestepensjonV2025Service
) {
    private val log = KotlinLogging.logger {}

    @PostMapping("/v2025/tjenestepensjon/v1/simulering")
    fun simuler(@RequestBody request: SimulerTjenestepensjonRequestDto): SimulerTjenestepensjonResponseDto {
        return tjenestepensjonV2025Service.simuler(request).fold(
            onSuccess = { aggregerRespons(it) },
            onFailure = { e ->
                log.error(e) { "Simulering feilet: ${e.message}" }
                SimulerTjenestepensjonResponseDto(ResultatTypeDto.ERROR, e.message)
            })
    }

    @GetMapping("/v2025/tjenestepensjon/ping")
    fun ping(): List<PingResponse> {
        return tjenestepensjonV2025Service.ping()
    }
}