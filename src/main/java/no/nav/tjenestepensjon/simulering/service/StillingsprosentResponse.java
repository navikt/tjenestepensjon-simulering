package no.nav.tjenestepensjon.simulering.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class StillingsprosentResponse {
    private final Map<TPOrdning, List<Stillingsprosent>> tpOrdningListMap;
    private final List<ExecutionException> exceptions;

    public StillingsprosentResponse(
            Map<TPOrdning, List<Stillingsprosent>> tpOrdningListMap, List<ExecutionException> exceptions) {
        this.tpOrdningListMap = tpOrdningListMap;
        this.exceptions = exceptions;
    }

    public Map<TPOrdning, List<Stillingsprosent>> getTpOrdningListMap() {
        return tpOrdningListMap;
    }

    public List<ExecutionException> getExceptions() {
        return exceptions;
    }
}
