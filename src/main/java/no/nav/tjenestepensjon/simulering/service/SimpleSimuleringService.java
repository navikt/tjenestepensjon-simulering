package no.nav.tjenestepensjon.simulering.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.GenericStillingsprosentCallableException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;
import no.nav.tjenestepensjon.simulering.rest.SimuleringEndpoint;

@Service
public class SimpleSimuleringService implements SimuleringEndpoint.SimuleringService {

    private final StillingsprosentService stillingsprosentService;

    public SimpleSimuleringService(StillingsprosentService stillingsprosentService) {
        this.stillingsprosentService = stillingsprosentService;
    }

    @Override
    public OutgoingResponse simuler(IncomingRequest request) {
        OutgoingResponse response = createEmpyResponse();
        try {
            StillingsprosentResponse stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(request.getFnr());
            handleStillingsprosentExceptions(response, stillingsprosentResponse);
            TPOrdning latest = stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.getTpOrdningListMap());
        } catch (DuplicateStillingsprosentEndDateException e) {
            SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
            simulertPensjon.setStatus("FEIL");
            simulertPensjon.setFeilkode("PARF");
            return response;
        } catch (MissingStillingsprosentException e) {
            SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
            simulertPensjon.setStatus("FEIL");
            simulertPensjon.setFeilkode("IKKE");
            return response;
        } catch (NoTpOrdningerFoundException e) {
            return new OutgoingResponse();
        }

        return response;
    }

    private void handleStillingsprosentExceptions(OutgoingResponse response, StillingsprosentResponse stillingsprosentResponse) {
        response.getSimulertPensjonListe().get(0).setUtelatteTpnr(stillingsprosentResponse.getExceptions().stream()
                .filter(e -> e.getCause() instanceof GenericStillingsprosentCallableException)
                .map(e -> ((GenericStillingsprosentCallableException) e.getCause()).getTpnr()).collect(Collectors.toList()));
        if (stillingsprosentResponse.getTpOrdningListMap().size() == 0) {
            throw new NullPointerException("Could not get response fom any TP-Providers");
        }
    }

    private OutgoingResponse createEmpyResponse() {
        OutgoingResponse response = new OutgoingResponse();
        SimulertPensjon simulertPensjon = new SimulertPensjon();
        response.setSimulertPensjonListe(List.of(simulertPensjon));
        return response;
    }
}
