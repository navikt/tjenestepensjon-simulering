package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.exceptions.SoapFaultException
import no.nav.tjenestepensjon.simulering.v1.models.error.StelvioFault
import org.slf4j.LoggerFactory
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.springframework.stereotype.Component
import org.springframework.ws.WebServiceMessage
import org.springframework.ws.client.core.FaultMessageResolver
import org.springframework.ws.soap.SoapMessage
import javax.xml.bind.JAXBElement

@Component
class SoapFaultHandler constructor(private val jaxb2Marshaller: Jaxb2Marshaller) : FaultMessageResolver {
    override fun resolveFault(message: WebServiceMessage) =
            throw (message as SoapMessage).soapBody.fault.run {
                try {
                    faultDetail.detailEntries.next().source.let {
                        @Suppress("UNCHECKED_CAST")
                        jaxb2Marshaller.unmarshal(it) as JAXBElement<StelvioFault>
                    }.run {
                        SoapFaultException(value::class.qualifiedName!!, value.errorMessage).also {
                            LOG.warn("Resolved known fault from SoapFaultDetail: $it")
                        }
                    }
                } catch (ex: Exception) {
                    SoapFaultException(faultCode.toString(), faultStringOrReason).also {
                        LOG.warn("Could not resolve known error from SoapFaultDetail. Resolved from SoapFault: $it")
                    }
                }
            }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}