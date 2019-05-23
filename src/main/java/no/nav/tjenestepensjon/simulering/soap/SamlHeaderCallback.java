package no.nav.tjenestepensjon.simulering.soap;

import java.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;

public class SamlHeaderCallback implements WebServiceMessageCallback {

    private final String token;
    private final Transformer transformer;

    public SamlHeaderCallback(String token) {
        this.token = token;
        try {
            this.transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws TransformerException {
        Assert.isInstanceOf(SoapMessage.class, message);
        SoapMessage soapMessage = (SoapMessage) message;
        SoapHeaderElement header =
                soapMessage.getSoapHeader().addHeaderElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"));
        transformer.transform(new StringSource(new String(Base64.getDecoder().decode(token))), header.getResult());
    }
}
