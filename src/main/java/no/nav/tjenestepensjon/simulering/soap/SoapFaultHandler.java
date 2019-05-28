package no.nav.tjenestepensjon.simulering.soap;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
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
        SoapMessage soapMessage = (SoapMessage) message;
        SoapFaultDetailElement soapFaultDetail = soapMessage.getSoapBody().getFault().getFaultDetail().getDetailEntries().next();
        JAXBElement jaxbElement = (JAXBElement) jaxb2Marshaller.unmarshal(soapFaultDetail.getSource());
        StelvioFault knownFault = (StelvioFault) jaxbElement.getValue();
        SoapFaultException resolved = new SoapFaultException(knownFault.getClass(), knownFault.getErrorMessage());
        LOG.warn("Resolved fault from SOAP-invocation: {}", resolved.toString());
        throw resolved;
    }

    static class SoapFaultException extends RuntimeException {
        private Class exception;
        private String message;

        public SoapFaultException(Class original, String originalMessage) {
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
