package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender

@Configuration
class SoapClientConfig(
    @Value("\${provider.uri}") val providerUri: String,
    private val fssGatewayAuthService: FssGatewayAuthService,
) {

    @Bean
    fun jaxb2Marshaller() = Jaxb2Marshaller().apply {
        setClassesToBeBound(
                XMLHentStillingsprosentListeRequestWrapper::class.java,
                XMLHentStillingsprosentListeResponseWrapper::class.java,
                XMLStillingsprosent::class.java)
    }

    @Bean
    fun webServiceTemplate(jaxb2Marshaller: Jaxb2Marshaller) =
        WebServiceTemplate().apply {
            defaultUri = providerUri
            marshaller = jaxb2Marshaller
            unmarshaller = jaxb2Marshaller
            faultMessageResolver = SoapFaultHandler(jaxb2Marshaller)
            interceptors = arrayOf(
                AuthAttachingHttpRequestInterceptor(fssGatewayAuthService),
            )
            setMessageSender(HttpUrlConnectionMessageSender())
            setCheckConnectionForFault(true)
            setCheckConnectionForError(true)
        }

    companion object {
        const val ENCODING = "UTF-8"
    }
}