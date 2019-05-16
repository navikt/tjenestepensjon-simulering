package no.nav.tjenestepensjon.simulering.exceptions;

import java.util.concurrent.ExecutionException;

public class GenericStillingsprosentCallableException extends ExecutionException {

    private final String tpnr;

    public GenericStillingsprosentCallableException(String msg, String tpnr) {
        super(msg);
        this.tpnr = tpnr;
    }

    public String getTpnr() {
        return tpnr;
    }
}
