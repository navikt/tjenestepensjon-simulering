package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.v1.consumer.TokenClientOld
import no.nav.tjenestepensjon.simulering.v1.models.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

@Service
@Controller
class SoapClient(
        webServiceTemplate: WebServiceTemplate,
        private val tokenClientOld: TokenClientOld
) : WebServiceGatewaySupport(), Tjenestepensjonsimulering {

    init {
        this.webServiceTemplate = webServiceTemplate
    }

    @Value("\${TJENESTEPENSJON_URL}")
    lateinit var simulerOffentlingTjenestepensjonUrl: String

    @Value("\${STILLINGSPROSENT_URL}")
    lateinit var hentStillingsprosentUrl: String

    @Autowired
    lateinit var samlConfig: SamlConfig

    override fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ) =
            webServiceTemplate.marshalSendAndReceive(
                    HentStillingsprosentListeRequest(fnr, tpOrdning)
                            .let(SOAPAdapter::marshal),
                    SOAPCallback(
                            hentStillingsprosentUrl,
                            tpLeverandor.stillingsprosentUrl,
                            tokenClientOld.samlAccessToken.accessToken,
                            samlConfig
                    )
            ).let {
                SOAPAdapter.unmarshal(it as XMLHentStillingsprosentListeResponseWrapper)
            }.stillingsprosentListe

    override fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ) =
            webServiceTemplate.marshalSendAndReceive(
                    with(request.simuleringsperioder) {
                        if (size > 1 && minOrNull()!!.isGradert())
                            SimulerOffentligTjenestepensjonRequest(
                                    simulerPensjonRequest = request,
                                    tpOrdning = tpOrdning,
                                    tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap,
                                    forsteUttak = minOrNull()!!,
                                    heltUttak = maxOrNull()!!
                            )
                        else
                            SimulerOffentligTjenestepensjonRequest(
                                    simulerPensjonRequest = request,
                                    tpOrdning = tpOrdning,
                                    tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap,
                                    forsteUttak = minOrNull()!!
                            )
                    }.let(SOAPAdapter::marshal),
                    SOAPCallback(
                            simulerOffentlingTjenestepensjonUrl,
                            tpLeverandor.simuleringUrl,
                            tokenClientOld.samlAccessToken.accessToken,
                            samlConfig
                    )
            ).let {
                SOAPAdapter.unmarshal(it as XMLSimulerOffentligTjenestepensjonResponseWrapper, request.fnr)
            }.simulertPensjonListe

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}