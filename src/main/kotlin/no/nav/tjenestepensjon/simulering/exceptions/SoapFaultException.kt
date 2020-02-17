package no.nav.tjenestepensjon.simulering.exceptions

class SoapFaultException(exception: String, message: String) : SimuleringException("IKKE", "SoapFaultException{exception=$exception, message='$message'}")