package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulerOffentligTjenestepensjonResponse


interface SimuleringService {
    fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest): SimulerOffentligTjenestepensjonResponse
}