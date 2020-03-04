package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulerOffentligTjenestepensjonResponse


interface SimuleringService {
    fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest): SimulerOffentligTjenestepensjonResponse
}