package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import java.util.concurrent.ExecutionException

data class SimulerPensjonResponse(val simulertPensjon: SimulertPensjon?, val exceptions: List<ExecutionException>?)