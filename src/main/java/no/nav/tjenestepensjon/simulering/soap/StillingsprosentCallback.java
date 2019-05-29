package no.nav.tjenestepensjon.simulering.soap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerException;

import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.addressing.client.ActionCallback;
import org.springframework.ws.soap.addressing.version.Addressing10;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

public class StillingsprosentCallback implements WebServiceMessageCallback {

    private final ActionCallback wsAddressingCallback;
    private final SoapActionCallback soapActionCallback;
    private final SamlHeaderCallback samlHeaderCallback;
    private final String action;

    public StillingsprosentCallback(String action, String tpLeverandorUrl, String samlToken) {
        try {
            this.wsAddressingCallback = new ActionCallback(new URI(action), new Addressing10(), new URI(tpLeverandorUrl));
            this.soapActionCallback = new SoapActionCallback(action);
            this.samlHeaderCallback = new SamlHeaderCallback(samlToken);
            this.action = action;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        Assert.isInstanceOf(SoapMessage.class, message);
        wsAddressingCallback.doWithMessage(message);
//        soapActionCallback.doWithMessage(message);
        samlHeaderCallback.doWithMessage(message);
        addSoapActionHttpHeader(action);
    }

    private void addSoapActionHttpHeader(String action) {
        TransportContext transportContext = TransportContextHolder.getTransportContext();
        HttpUrlConnection connection = (HttpUrlConnection) transportContext.getConnection();
        try {
            connection.addRequestHeader("SOAPAction", action);
        } catch (IOException e) {
            throw new RuntimeException("Exception while adding HTTP-header to SOAP-request", e);
        }
    }
}