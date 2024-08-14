package no.nav.tjenestepensjon.simulering.rest

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimulerTjenestepensjonResponseDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimuleringsResultatTypeDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.Tjenestepensjon2025Mapper.mapToVellykketTjenestepensjonSimuleringResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TPOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TjenestepensjonSimuleringV2025Controller(
    private val tjenestepensjonV2025Service: TjenestepensjonV2025Service
) {

    @PostMapping("/v2025/tjenestepensjon/v1/simulering")
    fun simuler(@RequestBody request: SimulerTjenestepensjonRequestDto): SimulerTjenestepensjonResponseDto {
        try{
            val simulertTjenestepensjon = tjenestepensjonV2025Service.simuler(request)
            return mapToVellykketTjenestepensjonSimuleringResponse(simulertTjenestepensjon)
        } catch (e: BrukerErIkkeMedlemException){
            return SimulerTjenestepensjonResponseDto(SimuleringsResultatTypeDto.ERROR, e.message)
        } catch (e: TPOrdningStoettesIkkeException){
            return SimulerTjenestepensjonResponseDto(SimuleringsResultatTypeDto.ERROR, e.message)
        } catch (e: TjenestepensjonSimuleringException){
            return SimulerTjenestepensjonResponseDto(SimuleringsResultatTypeDto.ERROR, e.message)
        }
    }
}