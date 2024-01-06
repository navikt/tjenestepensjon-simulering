package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.domain.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.ws.client.core.WebServiceTemplate

@Configuration
class SoapClientConfig(
        @Value("\${provider.uri}") val providerUri: String
) {

    @Bean
    fun jaxb2Marshaller() = Jaxb2Marshaller().apply {
        setClassesToBeBound(
                XMLHentStillingsprosentListeRequestWrapper::class.java,
                XMLSimulerOffentligTjenestepensjonRequestWrapper::class.java,
                XMLHentStillingsprosentListeResponseWrapper::class.java,
                XMLSimulerOffentligTjenestepensjonResponseWrapper::class.java,
                XMLSimuleringsdata::class.java,
                XMLSimulertAFPPrivat::class.java,
                XMLSimulertAP2011::class.java,
                XMLSimulertPensjon::class.java,
                XMLStillingsprosent::class.java,
                XMLTpForhold::class.java,
                XMLUtbetalingsperiode::class.java)
        //setMarshallerProperties(mapOf(JAXB_ENCODING to ENCODING))
        // setPackagesToScan("no.nav.tjenestepensjon.simulering.v1.soap.marshalling")
    }

    @Bean
    fun webServiceTemplate(jaxb2Marshaller: Jaxb2Marshaller) = WebServiceTemplate().apply {
        defaultUri = providerUri
        marshaller = jaxb2Marshaller
        unmarshaller = jaxb2Marshaller
        faultMessageResolver = SoapFaultHandler(jaxb2Marshaller)
        interceptors = arrayOf(NorwegianSoapResponseInterceptor())
        setCheckConnectionForFault(true)
        setCheckConnectionForError(true)
    }

    companion object {
        const val ENCODING = "UTF-8"
    }
}