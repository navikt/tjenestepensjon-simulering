package no.nav.tjenestepensjon.simulering.soap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import com.sun.xml.messaging.saaj.soap.ver1_1.Message1_1Impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.context.DefaultTransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

@ExtendWith(MockitoExtension.class)
class StillingsprosentCallbackTest {

    private String token = "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"></saml2:Assertion>\n";
    private StillingsprosentCallback callback = new StillingsprosentCallback("soapAction", "tpLeverandorUrl", new String(Base64.getEncoder().encode(token.getBytes())));

    @BeforeAll
    static void beforeAll() {
        DefaultTransportContext defaultTransportContext = mock(DefaultTransportContext.class);
        when(defaultTransportContext.getConnection()).thenReturn(mock(HttpUrlConnection.class));
        TransportContextHolder.setTransportContext(defaultTransportContext);
    }

    @Test
    void shouldAddWsAddressingTo() throws IOException, TransformerException {
        String wsAddressingQname = "{http://www.w3.org/2005/08/addressing}To";

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(wsAddressingQname)).hasNext(), is(true));
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(wsAddressingQname)).next().getText(), is("tpLeverandorUrl"));
    }

    @Test
    void shouldNotAddWsAddressingAction() throws IOException, TransformerException {
        String addressActionQname = "{http://www.w3.org/2005/08/addressing}Action";

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(addressActionQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(addressActionQname)).hasNext(), is(true));
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(addressActionQname)).next().getText(), is(nullValue()));
    }

    @Test
    void shouldAddSoapActionToMessage() throws IOException, TransformerException {
        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());

        callback.doWithMessage(message);
        assertThat(message.getSoapAction(), is("\"soapAction\""));
    }

    @Test
    void shouldAddSamlTokenHeader() throws IOException, TransformerException {
        String securityHeaderQname = "{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}Security";

        SoapMessage message = new SaajSoapMessage(new Message1_1Impl());
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext(), is(false));

        callback.doWithMessage(message);
        assertThat(message.getSoapHeader().examineHeaderElements(QName.valueOf(securityHeaderQname)).hasNext(), is(true));
    }
}
