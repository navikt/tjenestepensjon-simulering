package no.nav.tjenestepensjon.simulering.v1.soap

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.service.SamlTokenService
import no.nav.tjenestepensjon.simulering.sporingslogg.Organisasjon
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingsloggService
import no.nav.tjenestepensjon.simulering.v1.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import org.springframework.beans.factory.annotation.Autowired
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
class SoapClient(
    webServiceTemplate: WebServiceTemplate, private val samlTokenService: SamlTokenService,
    private val sporingsloggService: SporingsloggService
) : WebServiceGatewaySupport(), Tjenestepensjonsimulering {
    private val log = KotlinLogging.logger {}

    init {
        this.webServiceTemplate = webServiceTemplate
    }

    @Value("\${stillingsprosent.url}")
    lateinit var hentStillingsprosentUrl: String

    @Autowired
    lateinit var samlConfig: SamlConfig

    override fun getStillingsprosenter(
        fnr: String, tpOrdning: TPOrdningIdDto, tpLeverandor: TpLeverandor
    ): List<Stillingsprosent> {
        val dto = HentStillingsprosentListeRequest(FNR(fnr), tpOrdning)
        sporingsloggService.loggUtgaaendeRequest(Organisasjon.SPK, fnr, dto) //Det kalles kun SPK - må oppdateres med riktig organisasjon, hvis flere organisasjoner vil levere stillingsprosenter
        try {
            return webServiceTemplate.marshalSendAndReceive(
                dto.let(SOAPAdapter::marshal), SOAPCallback(
                    hentStillingsprosentUrl,
                    tpLeverandor.stillingsprosentUrl,
                    samlTokenService.samlAccessToken.accessToken,
                    samlConfig
                )
            ).let {
                SOAPAdapter.unmarshal(it as XMLHentStillingsprosentListeResponseWrapper)
            }.stillingsprosentListe
        } catch (ex: WebServiceTransportException) {
            log.error(ex) { "Transport error occurred while calling getStillingsprosenter: ${ex.message}" }
            throw ex
        } catch (ex: SoapFaultClientException) {
            // Handle SOAP faults returned from the server
            log.error(ex) { "SOAP fault occurred at getStillingsprosenter: ${ex.faultStringOrReason}" }
            // Optionally, return a custom response or throw a custom exception
            throw ex
        } catch (ex: WebServiceIOException) {
            // Handle IO exceptions related to SOAP calls (e.g., timeout)
            log.error(ex) { "IO error occurred while calling getStillingsprosenter: ${ex.message}" }
            throw ex
        } catch (ex: Exception) {
            log.error(ex) { "Unexpected error occurred while calling getStillingsprosenter: ${ex.message}" }
            throw ex
        }
    }
}
