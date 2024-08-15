package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.TjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import org.springframework.stereotype.Service

@Service
class KLPTjenestepensjonClient : TjenestepensjonV2025Client {
    override fun simuler(request: TjenestepensjonRequest): SimulertTjenestepensjon {
        return SimulertTjenestepensjon(request.fnr)
    }
}