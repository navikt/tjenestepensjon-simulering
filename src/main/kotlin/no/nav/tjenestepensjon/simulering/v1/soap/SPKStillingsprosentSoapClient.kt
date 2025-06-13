package no.nav.tjenestepensjon.simulering.v1.soap

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.service.SamlTokenService
import no.nav.tjenestepensjon.simulering.sporingslogg.Organisasjon
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingsloggService
import no.nav.tjenestepensjon.simulering.v1.StillingsprosentHenting
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ws.client.WebServiceIOException
import org.springframework.ws.client.WebServiceTransportException
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.client.core.support.WebServiceGatewaySupport
import org.springframework.ws.soap.client.SoapFaultClientException

@Service
@Controller
class SPKStillingsprosentSoapClient(
    private val webServiceTemplate: WebServiceTemplate,
    private val samlTokenService: SamlTokenService,
    private val sporingsloggService: SporingsloggService,
    @Value("\${stillingsprosent.url}") private val hentStillingsprosentUrl: String,
    @Value("\${oftp.before2025.spk.endpoint.stillingsprosentUrl}") private val url: String,
    private val samlConfig: SamlConfig
) : WebServiceGatewaySupport(), StillingsprosentHenting {
    private val log = KotlinLogging.logger {}

    override fun getStillingsprosenter(
        fnr: String, tpOrdning: TpOrdningFullDto
    ): List<Stillingsprosent> {
        val dto = HentStillingsprosentListeRequest(FNR(fnr), tpOrdning)
        sporingsloggService.loggUtgaaendeRequest(Organisasjon.SPK, fnr, dto)
        try {
            return webServiceTemplate.marshalSendAndReceive(
                dto.let(SOAPAdapter::marshal), SOAPCallback(
                    hentStillingsprosentUrl,
                    url,
                    samlTokenService.samlAccessToken.accessToken,
                    samlConfig
                )
            ).let {
                SOAPAdapter.unmarshal(it as XMLHentStillingsprosentListeResponseWrapper)
            }.stillingsprosentListe
        } catch (ex: WebServiceTransportException) {
            log.warn (ex) { "Transport error occurred while calling getStillingsprosenter: ${ex.message}" }
        } catch (ex: SoapFaultClientException) {
            // Handle SOAP faults returned from the server
            log.warn(ex) { "SOAP fault occurred at getStillingsprosenter: ${ex.faultStringOrReason}" }
            // Optionally, return a custom response or throw a custom exception
        } catch (ex: WebServiceIOException) {
            // Handle IO exceptions related to SOAP calls (e.g., timeout)
            log.warn(ex) { "IO error occurred while calling getStillingsprosenter: ${ex.message}" }
        } catch (ex: Exception) {
            log.warn(ex) { "Unexpected error occurred while calling getStillingsprosenter: ${ex.message}" }
        }
        return emptyList()
    }
}
