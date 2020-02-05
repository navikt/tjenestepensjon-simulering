package no.nav.tjenestepensjon.simulering.soap

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.ws.client.core.WebServiceTemplate

@Configuration
class SoapClientConfig {
    @Value("\${PROVIDER_URI}")
    private lateinit var providerUri: String

    @Bean
    fun jaxb2Marshaller() = Jaxb2Marshaller()
            .apply { contextPath = "no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1" }

    @Bean
    fun webServiceTemplate(soapFaultHandler: SoapFaultHandler) =
            WebServiceTemplate().apply {
                marshaller = jaxb2Marshaller()
                unmarshaller = jaxb2Marshaller()
                defaultUri = providerUri
                faultMessageResolver = soapFaultHandler
                setCheckConnectionForFault(false)
                setCheckConnectionForError(false)
            }
}