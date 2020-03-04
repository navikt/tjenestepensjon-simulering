package no.nav.tjenestepensjon.simulering.v2.service

import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse


interface SimuleringService {
    fun simulerOffentligTjenestepensjon(request: SimulerPensjonRequest): SimulerOffentligTjenestepensjonResponse
}