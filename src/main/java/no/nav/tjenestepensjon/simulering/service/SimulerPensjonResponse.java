package no.nav.tjenestepensjon.simulering.service;

import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SimulerPensjonResponse {

    private final OutgoingResponse.SimulertPensjon simulertPensjon;
    private final List<ExecutionException> exceptions;

    public SimulerPensjonResponse(OutgoingResponse.SimulertPensjon simulertPensjon, List<ExecutionException> exceptions) {
        this.simulertPensjon = simulertPensjon;
        this.exceptions = exceptions;
    }

    public OutgoingResponse.SimulertPensjon getSimulertPensjon() {
        return simulertPensjon;
    }

    public List<ExecutionException> getExceptions() {
        return exceptions;
    }
}
