package no.nav.tjenestepensjon.simulering.rest;

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestClient implements Tjenestepensjonsimulering {
    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning) {
        List<Stillingsprosent> stillingsprosenter = new ArrayList<>();

        return stillingsprosenter;
    }

    @Override
    public List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest incomingRequest, List<TPOrdning> tpOrdningList, TPOrdning latest) {
        var simuletPensjonListe = new ArrayList<OutgoingResponse.SimulertPensjon>();

        return simuletPensjonListe;
    }
}
