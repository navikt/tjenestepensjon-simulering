package no.nav.tjenestepensjon.simulering;

import java.util.List;
import java.util.Map;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;

public interface Tjenestepensjonsimulering {

    List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor) throws Exception;

    List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest request, TPOrdning tpOrdning, TpLeverandor tpLeverandor,
            Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap);
}
