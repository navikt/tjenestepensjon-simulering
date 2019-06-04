package no.nav.tjenestepensjon.simulering;

import java.util.List;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;

public interface Tjenestepensjonsimulering {

    List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning) throws Exception;

    List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest request, List<TPOrdning> tpOrdning, TPOrdning latest);
}
