package no.nav.tjenestepensjon.simulering.soap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Value("${PROVIDER_URI}")
    private String providerUri;

    @Bean
    Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath("no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1");
        return jaxb2Marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(SoapFaultHandler soapFaultHandler) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        webServiceTemplate.setDefaultUri(providerUri);
        webServiceTemplate.setFaultMessageResolver(soapFaultHandler);
        webServiceTemplate.setCheckConnectionForFault(false);
        return webServiceTemplate;
    }
}

