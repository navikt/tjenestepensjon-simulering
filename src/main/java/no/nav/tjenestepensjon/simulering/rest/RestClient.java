package no.nav.tjenestepensjon.simulering.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

@Component
public class RestClient implements Tjenestepensjonsimulering {
    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor) {
        List<Stillingsprosent> stillingsprosenter = new ArrayList<>();

        return stillingsprosenter;
    }

    @Override
    public List<SimulertPensjon> simulerPensjon(IncomingRequest incomingRequest, TPOrdning tpOrdning, TpLeverandor tpLeverandor,
            Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap) {
        var simulertPensjonListe = new ArrayList<SimulertPensjon>();

        return simulertPensjonListe;
    }
}
