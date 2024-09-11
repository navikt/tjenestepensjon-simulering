package no.nav.tjenestepensjon.simulering.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.Tjenestepensjon2025Mapper.mapToVellykketTjenestepensjonSimuleringResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimulerTjenestepensjonResponseDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimuleringsResultatTypeDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Service
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
        try {
            return tjenestepensjonV2025Service.simuler(request).fold(
                onSuccess = { data ->
                    mapToVellykketTjenestepensjonSimuleringResponse(data)
                },
                onFailure = { e ->
                    log.error(e) { "Simulering feilet: ${e.message}" }
                    SimulerTjenestepensjonResponseDto(SimuleringsResultatTypeDto.ERROR, "Simulering feilet")
                })
        } catch (e: BrukerErIkkeMedlemException) {
            return SimulerTjenestepensjonResponseDto(SimuleringsResultatTypeDto.ERROR, e.message)
        } catch (e: TpOrdningStoettesIkkeException) {
            return SimulerTjenestepensjonResponseDto(SimuleringsResultatTypeDto.ERROR, e.message)
        }
    }
}