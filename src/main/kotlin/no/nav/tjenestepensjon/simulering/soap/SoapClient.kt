package no.nav.tjenestepensjon.simulering.soap

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.v1.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerOffentligTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.util.TPOrdningStillingsprosentMap
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

@Service
@Controller
class SoapClient(
        webServiceTemplate: WebServiceTemplate,
        private val tokenClient: TokenClient
) : WebServiceGatewaySupport(), Tjenestepensjonsimulering {

    init {
        this.webServiceTemplate = webServiceTemplate
    }

    @Value("\${TJENESTEPENSJON_URL}")
    lateinit var simulerOffentlingTjenestepensjonUrl: String

    @Value("\${STILLINGSPROSENT_URL}")
    lateinit var hentStillingsprosentUrl: String

    @Value("\${SECURITY_CONTEXT_URL}")
    lateinit var samlSecurityContextUrl: String

    @Autowired
    lateinit var samlConfig: SamlConfig

    override fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ) =
            (webServiceTemplate.marshalSendAndReceive(
                    HentStillingsprosentListeRequest(fnr, tpOrdning),
                    SOAPCallback(
                            hentStillingsprosentUrl,
                            tpLeverandor.url,
                            tokenClient.samlAccessToken.accessToken,
                            samlConfig
                    )
            ) as HentStillingsprosentListeResponse).stillingsprosentListe

    override fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: TPOrdningStillingsprosentMap
    ) =
            (webServiceTemplate.marshalSendAndReceive(
                    with(request.simuleringsperioder) {
                        if (size > 1 && min()!!.isGradert())
                            SimulerOffentligTjenestepensjonRequest(
                                    simulerPensjonRequest = request,
                                    tpOrdning = tpOrdning,
                                    tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap,
                                    forsteUttak = min()!!,
                                    heltUttak = max()!!
                            )
                        else
                            SimulerOffentligTjenestepensjonRequest(
                                    simulerPensjonRequest = request,
                                    tpOrdning = tpOrdning,
                                    tpOrdningStillingsprosentMap = tpOrdningStillingsprosentMap,
                                    forsteUttak = min()!!
                            )
                    }.also { LOG.info("Mapped SimulerPensjonRequest: {} to SimulerOffentligTjenestepensjon: {}", request, it) },
                    SOAPCallback(
                            simulerOffentlingTjenestepensjonUrl,
                            tpLeverandor.url,
                            tokenClient.samlAccessToken.accessToken!!,
                            samlConfig
                    )
            ) as SimulerOffentligTjenestepensjonResponse).simulertPensjonListe

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}