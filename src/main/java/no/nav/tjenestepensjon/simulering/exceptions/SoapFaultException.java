package no.nav.tjenestepensjon.simulering.exceptions;

public class SoapFaultException extends RuntimeException {

    public SoapFaultException(String exception, String message) {
        super("SoapFaultException{" +
                "exception=" + exception +
                ", message='" + message + '\'' +
                '}');
    }
}
