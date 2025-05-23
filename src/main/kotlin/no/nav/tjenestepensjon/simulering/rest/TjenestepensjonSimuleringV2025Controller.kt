package no.nav.tjenestepensjon.simulering.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.Tjenestepensjon2025Aggregator.aggregerVellykketRespons
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.ResultatTypeDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.SimulerTjenestepensjonResponseDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.*
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Service
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class TjenestepensjonSimuleringV2025Controller(
    private val tjenestepensjonV2025Service: TjenestepensjonV2025Service
) {
    private val log = KotlinLogging.logger {}

    @PostMapping("/v2025/tjenestepensjon/v1/simulering")
    fun simuler(@RequestBody request: SimulerTjenestepensjonRequestDto): SimulerTjenestepensjonResponseDto {
        log.debug { "Simulerer tjenestepensjon for request: $request" }
        val simuleringsresultat = tjenestepensjonV2025Service.simuler(request)
        val relevanteTpOrdninger = simuleringsresultat.first
        return simuleringsresultat.second.fold(
            onSuccess = {
                val aggregerVellykketRespons: SimulerTjenestepensjonResponseDto = aggregerVellykketRespons(it, relevanteTpOrdninger)
                log.debug { "Simulering vellykket: $aggregerVellykketRespons" }
                aggregerVellykketRespons
            },
            onFailure = { e ->
                when (e) {
                    is BrukerErIkkeMedlemException -> SimulerTjenestepensjonResponseDto(ResultatTypeDto.BRUKER_ER_IKKE_MEDLEM_HOS_TP_ORDNING, e.message, relevanteTpOrdninger)
                    is TpOrdningStoettesIkkeException -> SimulerTjenestepensjonResponseDto(ResultatTypeDto.TP_ORDNING_ER_IKKE_STOTTET, e.message, relevanteTpOrdninger)
                    is TjenestepensjonSimuleringException -> SimulerTjenestepensjonResponseDto(ResultatTypeDto.TEKNISK_FEIL_FRA_TP_ORDNING, e.message, relevanteTpOrdninger)
                    is TomSimuleringFraTpOrdningException -> SimulerTjenestepensjonResponseDto(ResultatTypeDto.INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING, "Simulering fra ${e.tpOrdning} inneholder ingen utbetalingsperioder", relevanteTpOrdninger)
                    is IkkeSisteOrdningException -> SimulerTjenestepensjonResponseDto(ResultatTypeDto.INGEN_UTBETALINGSPERIODER_FRA_TP_ORDNING, "Simulering fra ${e.tpOrdning} inneholder ingen utbetalingsperioder", relevanteTpOrdninger)
                    is TpregisteretException -> loggOgReturnerTekniskFeil(e)
                    else -> loggOgReturnerTekniskFeil(RuntimeException(e))
                }
            })
    }

    private fun loggOgReturnerTekniskFeil(e: RuntimeException): SimulerTjenestepensjonResponseDto {
        log.error(e) { "Simulering feilet: ${e.message}" }
        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
    }

    @GetMapping("/v2025/tjenestepensjon/ping")
    fun ping(): List<PingResponse> {
        return tjenestepensjonV2025Service.ping()
    }
}