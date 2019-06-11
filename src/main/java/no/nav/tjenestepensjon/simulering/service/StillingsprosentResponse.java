package no.nav.tjenestepensjon.simulering.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class StillingsprosentResponse {
    private final Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap;
    private final List<ExecutionException> exceptions;

    public StillingsprosentResponse(
            Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap, List<ExecutionException> exceptions) {
        this.tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap;
        this.exceptions = exceptions;
    }

    public Map<TPOrdning, List<Stillingsprosent>> getTpOrdningStillingsprosentMap() {
        return tpOrdningStillingsprosentMap;
    }

    public List<ExecutionException> getExceptions() {
        return exceptions;
    }
}
