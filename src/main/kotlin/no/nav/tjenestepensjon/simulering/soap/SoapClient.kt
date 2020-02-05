package no.nav.tjenestepensjon.simulering.soap

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering
import no.nav.tjenestepensjon.simulering.config.ApplicationProperties.HENT_STILLINGSPROSENT_URL
import no.nav.tjenestepensjon.simulering.config.ApplicationProperties.SIMULER_OFFENTLIG_TJENESTEPENSJON_URL
import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.mapper.SoapMapper
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.client.core.support.WebServiceGatewaySupport

@Component
class SoapClient(webServiceTemplate: WebServiceTemplate, private val tokenClient: TokenClient) : WebServiceGatewaySupport(), Tjenestepensjonsimulering {

    init {
        this.webServiceTemplate = webServiceTemplate
    }

    override fun getStillingsprosenter(
            fnr: FNR,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor
    ) =
            (webServiceTemplate.marshalSendAndReceive(
                    SoapMapper.mapStillingsprosentRequest(fnr, tpOrdning),
                    SOAPCallback(
                            HENT_STILLINGSPROSENT_URL,
                            tpLeverandor.url,
                            tokenClient.samlAccessToken.accessToken!!
                    )
            ) as HentStillingsprosentListeResponse).stillingsprosentListe

    override fun simulerPensjon(
            request: SimulerPensjonRequest,
            tpOrdning: TPOrdning,
            tpLeverandor: TpLeverandor,
            tpOrdningStillingsprosentMap: Map<TPOrdning, List<Stillingsprosent>>
    ) =
            (webServiceTemplate.marshalSendAndReceive(
                    SoapMapper.mapSimulerTjenestepensjonRequest(request, tpOrdning, tpOrdningStillingsprosentMap),
                    SOAPCallback(
                            SIMULER_OFFENTLIG_TJENESTEPENSJON_URL,
                            tpLeverandor.url,
                            tokenClient.samlAccessToken.accessToken!!
                    )
            ) as SimulerOffentligTjenestepensjonResponse).simulertPensjonListe

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

}