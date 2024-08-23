package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.service.TokenService
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

@Service
@Controller
class SoapClient(
    webServiceTemplate: WebServiceTemplate, private val tokenService: TokenService
) : WebServiceGatewaySupport(), Tjenestepensjonsimulering {

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
    ) = webServiceTemplate.marshalSendAndReceive(
        HentStillingsprosentListeRequest(fnr, tpOrdning).let(SOAPAdapter::marshal), SOAPCallback(
            hentStillingsprosentUrl,
            tpLeverandor.stillingsprosentUrl,
            tokenService.samlAccessToken.accessToken,
            samlConfig
        )
    ).let {
        SOAPAdapter.unmarshal(it as XMLHentStillingsprosentListeResponseWrapper)
    }.stillingsprosentListe

    override fun simulerPensjon(
        request: SimulerPensjonRequestV1,
        tpOrdning: TPOrdningIdDto,
        tpLeverandor: TpLeverandor,
        tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ) = webServiceTemplate.marshalSendAndReceive(
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

}
