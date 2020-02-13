package no.nav.tjenestepensjon.simulering.soap

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.ws.client.core.WebServiceTemplate

@Configuration
class SoapClientConfig(
        @Value("\${PROVIDER_URI}") val providerUri: String
) {

    @Bean
    fun jaxb2Marshaller() = Jaxb2Marshaller()
            .apply {
                setPackagesToScan("no.nav.tjenestepensjon.simulering.model.v1")
            }

    @Bean
    fun webServiceTemplate() =
            WebServiceTemplate().apply {
                marshaller = jaxb2Marshaller()
                unmarshaller = jaxb2Marshaller()
                defaultUri = providerUri
                faultMessageResolver = SoapFaultHandler(jaxb2Marshaller())
                setCheckConnectionForFault(false)
                setCheckConnectionForError(false)
            }
}