package no.nav.tjenestepensjon.simulering.soap;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.HentStillingsprosentListeResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerOffentligTjenestepensjonResponse;
import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.mapper.AFPPrivatMapper;
import no.nav.tjenestepensjon.simulering.mapper.StillingsprosentMapper;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SoapClient extends WebServiceGatewaySupport implements Tjenestepensjonsimulering {

    private final WebServiceTemplate webServiceTemplate;

    public SoapClient(WebServiceTemplate template) {
        this.webServiceTemplate = template;
    }

    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, String tpnr, String tssEksternId, String simuleringsKode) {
        var request = new ObjectFactory().createHentStillingsprosentListeRequest();
        request.setFnr(fnr);
        request.setTpnr(tpnr);
        request.setTssEksternId(tssEksternId);
        request.setSimuleringsKode(simuleringsKode);

        var response = (HentStillingsprosentListeResponse) webServiceTemplate.marshalSendAndReceive(request);

        return response.getStillingsprosentListe().stream()
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
