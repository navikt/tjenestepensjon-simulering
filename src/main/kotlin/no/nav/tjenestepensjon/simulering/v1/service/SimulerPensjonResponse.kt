package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import java.util.concurrent.ExecutionException

data class SimulerPensjonResponse(val simulertPensjon: SimulertPensjon?, val exceptions: List<ExecutionException>?)