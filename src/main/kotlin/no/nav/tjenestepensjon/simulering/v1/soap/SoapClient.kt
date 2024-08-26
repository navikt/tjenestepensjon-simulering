package no.nav.tjenestepensjon.simulering.v1.soap

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.service.TokenService
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper
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
    webServiceTemplate: WebServiceTemplate, private val tokenService: TokenService
) : WebServiceGatewaySupport(), Tjenestepensjonsimulering {
    private val log = KotlinLogging.logger {}

    init {
        this.webServiceTemplate = webServiceTemplate
    }

    @Value("\${tjenestepensjon.url}")
    lateinit var simulerOffentlingTjenestepensjonUrl: String

    @Value("\${stillingsprosent.url}")
    lateinit var hentStillingsprosentUrl: String

    @Autowired
    lateinit var samlConfig: SamlConfig

    override fun getStillingsprosenter(
        fnr: FNR, tpOrdning: TPOrdningIdDto, tpLeverandor: TpLeverandor
    ): List<Stillingsprosent> {
        try {
            return webServiceTemplate.marshalSendAndReceive(
                HentStillingsprosentListeRequest(fnr, tpOrdning).let(SOAPAdapter::marshal), SOAPCallback(
                    hentStillingsprosentUrl,
                    tpLeverandor.stillingsprosentUrl,
                    tokenService.samlAccessToken.accessToken,
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

    override fun simulerPensjon(
        request: SimulerPensjonRequestV1,
        tpOrdning: TPOrdningIdDto,
        tpLeverandor: TpLeverandor,
        tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ): List<SimulertPensjon> {
        try {
            return webServiceTemplate.marshalSendAndReceive(
                with(request.simuleringsperioder) {
                    if (size > 1 && minOrNull()!!.isGradert()) SimulerOffentligTjenestepensjonRequest(
                        simulerPensjonRequest = request,
                        tpOrdning = tpOrdning,
                        tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap,
                        forsteUttak = minOrNull()!!,
                        heltUttak = maxOrNull()!!
                    )
                    else SimulerOffentligTjenestepensjonRequest(
                        simulerPensjonRequest = request,
                        tpOrdning = tpOrdning,
                        tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap,
                        forsteUttak = minOrNull()!!
                    )
                }.let(SOAPAdapter::marshal), SOAPCallback(
                    simulerOffentlingTjenestepensjonUrl,
                    tpLeverandor.simuleringUrl,
                    tokenService.samlAccessToken.accessToken,
                    samlConfig
                )
            ).let {
                SOAPAdapter.unmarshal(it as XMLSimulerOffentligTjenestepensjonResponseWrapper, request.fnr)
            }.simulertPensjonListe
        } catch (ex: WebServiceTransportException) {
            log.error(ex) { "Transport error occurred while calling simulerPensjon: ${ex.message}" }
            throw ex
        } catch (ex: SoapFaultClientException) {
            // Handle SOAP faults returned from the server
            log.error(ex) { "SOAP fault occurred at simulerPensjon: ${ex.faultStringOrReason}" }
            // Optionally, return a custom response or throw a custom exception
            throw ex
        } catch (ex: WebServiceIOException) {
            // Handle IO exceptions related to SOAP calls (e.g., timeout)
            log.error(ex) { "IO error occurred while calling simulerPensjon: ${ex.message}" }
            throw ex
        } catch (ex: Exception) {
            log.error(ex) { "Unexpected error occurred while calling simulerPensjon: ${ex.message}" }
            throw ex
        }
    }
}
