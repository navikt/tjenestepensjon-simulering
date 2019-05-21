package no.nav.tjenestepensjon.simulering;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.GenericStillingsprosentCallableException;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;

import java.util.List;

public interface Tjenestepensjonsimulering {

    List<Stillingsprosent> getStillingsprosenter(String fnr, String tpnr, String tssEksternId, String simuleringsKode) throws GenericStillingsprosentCallableException;

    List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest request, TPOrdning tpOrdning);
}
