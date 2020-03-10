package no.nav.tjenestepensjon.simulering.exceptions

class MissingEndpointServiceException(exception: String) : SimuleringException("IKKE", "SoapFaultException{exception=$exception}")