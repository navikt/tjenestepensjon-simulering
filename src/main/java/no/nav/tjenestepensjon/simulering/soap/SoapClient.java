package no.nav.tjenestepensjon.simulering.soap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerOffentligTjenestepensjonResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListe;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListeResponse;
import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.consumer.TokenClient;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.mapper.AFPPrivatMapper;
import no.nav.tjenestepensjon.simulering.mapper.StillingsprosentMapper;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;

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
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning) throws Exception {
        HentStillingsprosentListe wrapperRequest = new HentStillingsprosentListe();
        var request = new ObjectFactory().createHentStillingsprosentListeRequest();
        request.setFnr(fnr);
        request.setTpnr(tpOrdning.getTpId());
        request.setTssEksternId(tpOrdning.getTssId());
        request.setSimuleringsKode("AP");
        wrapperRequest.setRequest(request);

        var response = (HentStillingsprosentListeResponse) webServiceTemplate.marshalSendAndReceive(wrapperRequest,
                new StillingsprosentCallback(
                        "http://nav.no/ekstern/pensjon/tjenester/tjenestepensjonSimulering/v1/TjenestepensjonSimuleringWSEXP_TjenestepensjonSimuleringHttpService/hentStillingsprosentListeRequest",
                        tpOrdning.getTpLeverandor().getUrl(),
                        tokenClient.getSamlAccessToken().getAccessToken()));

        return response.getResponse().getStillingsprosentListe().stream()
                .map(new StillingsprosentMapper()::mapToStillingsprosent)
                .collect(Collectors.toList());
    }

    @Override
    public List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest incomingRequest, TPOrdning tpOrdning) {
        var simulerTjenestepensjon = new ObjectFactory().createSimulerTjenestepensjon();
        simulerTjenestepensjon.setFnr(incomingRequest.getFnr());
        simulerTjenestepensjon.setTpnr(tpOrdning.getTpId());
        simulerTjenestepensjon.setTssEksternId(tpOrdning.getTssId());
        simulerTjenestepensjon.setSivilstandKode(incomingRequest.getSivilstandkode());
        simulerTjenestepensjon.setSprak(incomingRequest.getSprak());
        simulerTjenestepensjon.setSimulertAFPOffentlig(incomingRequest.getSimulertAFPOffentlig());
        simulerTjenestepensjon.setSimulertAFPPrivat(new AFPPrivatMapper()
                .mapToSimulertAFPPrivat(incomingRequest.getSimulertAFPPrivat()));
        // TODO: Lists
//        simulerTjenestepensjon.setForsteUttakDato();
//        simulerTjenestepensjon.setUttaksgrad();
//        simulerTjenestepensjon.setHeltUttakDato();
//        simulerTjenestepensjon.setStillingsprosentOffHeltUttak();
//        simulerTjenestepensjon.setStillingsprosentOffGradertUttak();
//        simulerTjenestepensjon.setInntektForUttak();
//        simulerTjenestepensjon.setInntektUnderGradertUttak();
//        simulerTjenestepensjon.setInntektEtterHeltUttak();
//        simulerTjenestepensjon.setAntallArInntektEtterHeltUttak();
//        simulerTjenestepensjon.setSimulertAP2011();

        var request = new ObjectFactory().createSimulerOffentligTjenestepensjonRequest();
        request.setSimulerTjenestepensjon(simulerTjenestepensjon);

        var response = (SimulerOffentligTjenestepensjonResponse) webServiceTemplate.marshalSendAndReceive(request);
        var simuletPensjonListe = new ArrayList<OutgoingResponse.SimulertPensjon>();

        for (var simulertPensjon : response.getSimulertPensjonListe()) {
            var mappedSimulertPensjon = new OutgoingResponse.SimulertPensjon();
            mappedSimulertPensjon.setTpnr(simulertPensjon.getTpnr());
            mappedSimulertPensjon.setNavnOrdning(simulertPensjon.getNavnOrdning());
            mappedSimulertPensjon.setInkluderteOrdninger(simulertPensjon.getInkludertOrdningListe());
            mappedSimulertPensjon.setLeverandorUrl(simulertPensjon.getLeverandorUrl());
//            mappedSimulertPensjon.setInkluderteTpnr();
//            mappedSimulertPensjon.setUtelatteTpnr();
//            mappedSimulertPensjon.setStatus();
//            mappedSimulertPensjon.setFeilkode();
//            mappedSimulertPensjon.setFeilbeskrivelse();
//            mappedSimulertPensjon.setUtbetalingsperioder();
            simuletPensjonListe.add(mappedSimulertPensjon);
        }

        return simuletPensjonListe;
    }
}
