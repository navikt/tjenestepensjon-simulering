package no.nav.tjenestepensjon.simulering.soap;

import static no.nav.tjenestepensjon.simulering.mapper.SoapMapper.mapSimulerTjenestepensjonRequest;
import static no.nav.tjenestepensjon.simulering.mapper.SoapMapper.mapSimulerTjenestepensjonResponse;
import static no.nav.tjenestepensjon.simulering.mapper.SoapMapper.mapStillingsprosentRequest;
import static no.nav.tjenestepensjon.simulering.mapper.SoapMapper.mapStillingsprosentResponse;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListeResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjonResponse;
import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.consumer.TokenClient;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

@Component
public class SoapClient extends WebServiceGatewaySupport implements Tjenestepensjonsimulering {

    private static final Logger LOG = LoggerFactory.getLogger(SoapClient.class);

    private final WebServiceTemplate webServiceTemplate;
    private final TokenClient tokenClient;

    public SoapClient(WebServiceTemplate template, TokenClient tokenClient) {
        this.webServiceTemplate = template;
        this.tokenClient = tokenClient;
    }

    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning) {
        var response = (HentStillingsprosentListeResponse) webServiceTemplate.marshalSendAndReceive(
                mapStillingsprosentRequest(fnr, tpOrdning),
                new StillingsprosentCallback(
                        "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1/TjenestepensjonSimulering/hentStillingsprosentListeRequest",
                        tpOrdning.getTpLeverandor().getUrl(),
                        tokenClient.getSamlAccessToken().getAccessToken()));
        return mapStillingsprosentResponse(response);
    }

    @Override
    public List<SimulertPensjon> simulerPensjon(IncomingRequest incomingRequest, TPOrdning tpOrdning) {
        var response = (SimulerOffentligTjenestepensjonResponse) webServiceTemplate.marshalSendAndReceive(
                mapSimulerTjenestepensjonRequest(incomingRequest, tpOrdning),
                new StillingsprosentCallback(
                        "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1/TjenestepensjonSimulering/simulerOffentligTjenestepensjonRequest",
                        tpOrdning.getTpLeverandor().getUrl(),
                        tokenClient.getSamlAccessToken().getAccessToken()));

        return mapSimulerTjenestepensjonResponse(response);
    }
}
