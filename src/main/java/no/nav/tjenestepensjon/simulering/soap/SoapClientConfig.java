package no.nav.tjenestepensjon.simulering.soap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import no.nav.tjenestepensjon.simulering.config.SamlHeaderInterceptor;

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
    public WebServiceTemplate webServiceTemplate(SamlHeaderInterceptor samlHeaderInterceptor) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        webServiceTemplate.setDefaultUri(providerUri);
        ClientInterceptor[] interceptors = new ClientInterceptor[] {samlHeaderInterceptor};
        webServiceTemplate.setInterceptors(interceptors);
        return webServiceTemplate;
    }
}

