package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.response.*

object Tjenestepensjon2025Mapper {

    fun mapToVellykketTjenestepensjonSimuleringResponse(simulertTjenestepensjon: SimulertTjenestepensjon) =
        SimulerTjenestepensjonResponseDto(
            simuleringsResultatStatus = SimuleringsResultatStatusDto(SimuleringsResultatTypeDto.SUCCESS),
            simuleringsResultatDto = SimuleringsResultatDto(
                utbetalingsperioder = simulertTjenestepensjon.utbetalingsperioder.map { UtbetalingsperiodeDto(it.fom, it.maanedligBelop, it.ytelseType) },
                aarsakIngenUtbetaling = simulertTjenestepensjon.aarsakIngenUtbetaling
            )
        )
}