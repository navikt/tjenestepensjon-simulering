package no.nav.tjenestepensjon.simulering.exceptions;

import java.util.concurrent.ExecutionException;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class StillingsprosentCallableException extends ExecutionException {

    private final TPOrdning tpOrdning;

    public StillingsprosentCallableException(String msg, Throwable cause, TPOrdning tpOrdning) {
        super(msg, cause);
        this.tpOrdning = tpOrdning;
    }

    public TPOrdning getTpOrdning() {
        return tpOrdning;
    }

    @Override
    public String toString() {
        return "StillingsprosentCallableException{" +
                "message'" + getLocalizedMessage() + '\'' +
                "tpOrdning='" + tpOrdning.toString() + '\'' +
                '}';
    }
}