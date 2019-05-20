package no.nav.tjenestepensjon.simulering;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;

import java.util.List;

public interface Tjenestepensjonsimulering {

    List<Stillingsprosent> getStillingsprosenter();

    List<OutgoingResponse.SimulertPensjon> simulerPensjon();
}
