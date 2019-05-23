package no.nav.tjenestepensjon.simulering.soap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl;

import org.junit.jupiter.api.Test;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

class StillingsprosentCallbackTest {

    private String token = "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"></saml2:Assertion>\n";

    @Test
    void shouldAddWsAddressing() throws IOException, TransformerException {
        String wsAddressingQname = "{http://www.w3.org/2005/08/addressing}To";
        StillingsprosentCallback callback = new StillingsprosentCallback("action", "url", new String(Base64.getEncoder().encode(token.getBytes())));

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext(), is(true));
    }

    @Test
    void shouldAddMessageId() throws IOException, TransformerException {
        String messageIdQname = "{http://www.w3.org/2005/08/addressing}MessageID";
        StillingsprosentCallback callback = new StillingsprosentCallback("action", "url", new String(Base64.getEncoder().encode(token.getBytes())));

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(messageIdQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(messageIdQname)).hasNext(), is(true));
    }

    @Test
    void shouldAddWsAddressingAction() throws IOException, TransformerException {
        String addressActionQname = "{http://www.w3.org/2005/08/addressing}Action";
        StillingsprosentCallback callback = new StillingsprosentCallback("action", "url", new String(Base64.getEncoder().encode(token.getBytes())));

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(addressActionQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(addressActionQname)).hasNext(), is(true));
    }

    @Test
    void shouldAddSamlTokenHeader() throws IOException, TransformerException {
        String securityHeaderQname = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security";
        StillingsprosentCallback callback = new StillingsprosentCallback("action", "url", new String(Base64.getEncoder().encode(token.getBytes())));

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext(), is(true));
    }
}
