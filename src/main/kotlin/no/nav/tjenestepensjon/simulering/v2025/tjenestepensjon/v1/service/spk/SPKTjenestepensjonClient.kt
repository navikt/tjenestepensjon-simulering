package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import org.springframework.stereotype.Service

@Service
class SPKTjenestepensjonClient : TjenestepensjonV2025Client {
    override fun simuler(request: SimulerTjenestepensjonRequestDto): SimulertTjenestepensjon {
        return SimulertTjenestepensjon()
    }
}