package no.nav.tjenestepensjon.simulering.soap;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapMessage;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.felles.v1.StelvioFault;

@Component
public class SoapFaultHandler implements FaultMessageResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SoapFaultHandler.class);
    private final Jaxb2Marshaller jaxb2Marshaller;

    public SoapFaultHandler(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @Override
    public void resolveFault(WebServiceMessage message) {
        SoapFaultException exception;
        SoapMessage soapMessage = (SoapMessage) message;
        SoapFault soapFault = soapMessage.getSoapBody().getFault();
        try {
            SoapFaultDetailElement soapFaultDetail = soapFault.getFaultDetail().getDetailEntries().next();
            JAXBElement jaxbElement = (JAXBElement) jaxb2Marshaller.unmarshal(soapFaultDetail.getSource());
            StelvioFault knownFault = (StelvioFault) jaxbElement.getValue();
            exception = new SoapFaultException(knownFault.getClass().getName(), knownFault.getErrorMessage());
            LOG.warn("Resolved known fault from SoapFaultDetail: {}", exception.toString());
        } catch (Exception ex) {
            exception = new SoapFaultException(soapFault.getFaultCode().toString(), soapFault.getFaultStringOrReason());
            LOG.warn("Could not resolve known error from SoapFaultDetail. Resolved from SaopFault: {}", exception.toString());
        }
        throw exception;
    }

    static class SoapFaultException extends RuntimeException {
        private String exception;
        private String message;

        public SoapFaultException(String original, String originalMessage) {
            this.exception = original;
            this.message = originalMessage;
        }

        @Override
        public String toString() {
            return "SoapFaultException{" +
                    "exception=" + exception +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
