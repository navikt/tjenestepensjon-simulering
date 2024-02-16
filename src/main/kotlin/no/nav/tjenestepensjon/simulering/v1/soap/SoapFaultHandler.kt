package no.nav.tjenestepensjon.simulering.v1.soap

import jakarta.xml.bind.JAXBElement
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.exceptions.SoapFaultException
import no.nav.tjenestepensjon.simulering.v1.models.error.StelvioFault
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.stereotype.Component
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.FaultMessageResolver
import org.springframework.ws.soap.SoapMessage

@Component
class SoapFaultHandler constructor(private val jaxb2Marshaller: Jaxb2Marshaller) : FaultMessageResolver {
    private val log = KotlinLogging.logger {}

    override fun resolveFault(message: WebServiceMessage) =
            throw (message as SoapMessage).soapBody.fault.run {
                try {
                    faultDetail.detailEntries.next().source.let {
                        @Suppress("UNCHECKED_CAST")
                        jaxb2Marshaller.unmarshal(it) as JAXBElement<StelvioFault>
                    }.run {
                        SoapFaultException(value::class.qualifiedName!!, value.errorMessage).also {
                            log.warn { "Resolved known fault from SoapFaultDetail: $it" }
                        }
                    }
                } catch (ex: Exception) {
                    SoapFaultException(faultCode.toString(), faultStringOrReason).also {
                        log.warn { "Could not resolve known error from SoapFaultDetail. Resolved from SoapFault: $it" }
                    }
                }
            }

}