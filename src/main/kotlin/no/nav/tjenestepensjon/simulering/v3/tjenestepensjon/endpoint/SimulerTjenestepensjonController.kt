package no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.endpoint

import no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.domain.SimulerTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.service.SimulerTjenestepensjonService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SimulerTjenestepensjonController(
    private val simulerTjenestepensjonService: SimulerTjenestepensjonService
) {

    @PostMapping("/2025/simulering")
    fun simuler(@RequestBody request: SimulerTjenestepensjonRequest) {
        // kun input-validering, resons-håndtering og exception-håndterting
        simulerTjenestepensjonService.simuler(request)
        return
    }
}