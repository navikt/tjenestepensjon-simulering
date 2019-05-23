package no.nav.tjenestepensjon.simulering.config;

import java.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringSource;

import no.nav.tjenestepensjon.simulering.consumer.TokenClient;

@Component
public class SamlHeaderInterceptor implements ClientInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(SamlHeaderInterceptor.class);

    private final Transformer transformer;
    private final TokenClient tokenClient;

    public SamlHeaderInterceptor(TokenClient tokenClient) throws TransformerConfigurationException {
        this.tokenClient = tokenClient;
        TransformerFactory tFactory = TransformerFactory.newInstance();
        transformer = tFactory.newTransformer();
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        LOG.info("INTERCEPTING REQUEST AND ADDING HEADER");
        SaajSoapMessage s = (SaajSoapMessage) messageContext.getRequest();
        SoapHeaderElement header = s.getSoapHeader().addHeaderElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"));
        String saml = tokenClient.getSamlAccessToken().getAccessToken();

        try {
            transformer.transform(new StringSource(new String(Base64.getDecoder().decode(saml))), header.getResult());
        } catch (TransformerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        return false;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        return false;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

    }
}
