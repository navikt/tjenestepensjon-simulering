package no.nav.tjenestepensjon.simulering.soap;

import javax.xml.transform.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.soap.SoapMessage;

public class SoapFaultHandler implements FaultMessageResolver {

    @Override
    public void resolveFault(WebServiceMessage message) {
        SoapMessage soapMessage = (SoapMessage) message;
        throw new SoapClientException(soapMessage);
    }

    static class SoapClientException extends RuntimeException {

        private static Logger LOG = LoggerFactory.getLogger(SoapClientException.class);

        private final Result soapFault;

        public SoapClientException(SoapMessage soapMessage) {
            soapFault = soapMessage.getSoapBody().getFault().getFaultDetail().getResult();
            LOG.error("FAULT IS: {}", soapFault);
        }
    }
}
