package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException

interface TjenestepensjonV2025Client {
    @Throws(TjenestepensjonSimuleringException::class)
    fun simuler(request: SimulerTjenestepensjonRequestDto, tpNummer: String): Result<SimulertTjenestepensjon>

    companion object {
        const val TJENESTE = "tjenestepensjonV2025"
    }
}