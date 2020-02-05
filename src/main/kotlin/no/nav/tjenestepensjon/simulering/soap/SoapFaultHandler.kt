package no.nav.tjenestepensjon.simulering.soap

import no.nav.tjenestepensjon.simulering.exceptions.SoapFaultException
import no.nav.tjenestepensjon.simulering.model.v1.error.StelvioFault
import org.slf4j.LoggerFactory
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.stereotype.Component
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.FaultMessageResolver
import org.springframework.ws.soap.SoapMessage
import javax.xml.bind.JAXBElement
import kotlin.reflect.full.cast

@Component
class SoapFaultHandler(private val jaxb2Marshaller: Jaxb2Marshaller) : FaultMessageResolver {
    override fun resolveFault(message: WebServiceMessage): Nothing =
            throw (message as SoapMessage).soapBody.fault.let { soapFault ->
                try {
                    val knownFault: StelvioFault = soapFault.faultDetail.detailEntries.next().source
                            .let(jaxb2Marshaller::unmarshal)
                            .let(JAXBElement::class::cast)
                            .value as StelvioFault
                    SoapFaultException(knownFault::class.qualifiedName!!, knownFault.errorMessage).also {
                        LOG.warn("Resolved known fault from SoapFaultDetail: {}", it.toString())
                    }
                } catch (ex: Exception) {
                    SoapFaultException(soapFault.faultCode.toString(), soapFault.faultStringOrReason).also {
                        LOG.warn("Could not resolve known error from SoapFaultDetail. Resolved from SaopFault: {}", it.toString())
                    }
                }
            }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}