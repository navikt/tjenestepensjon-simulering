package no.nav.tjenestepensjon.simulering;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException;

public class StillingsprosentCallable implements Callable<List<Stillingsprosent>> {

    private static final Logger LOG = LoggerFactory.getLogger(StillingsprosentCallable.class);
    private final TPOrdning tpOrdning;
    private final TpLeverandor tpLeverandor;
    private final String fnr;
    private final TjenestepensjonsimuleringEndpointRouter endpointRouter;

    public StillingsprosentCallable(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor, TjenestepensjonsimuleringEndpointRouter endpointRouter) {
        this.tpOrdning = tpOrdning;
        this.fnr = fnr;
        this.tpLeverandor = tpLeverandor;
        this.endpointRouter = endpointRouter;
    }

    @Override
    public List<Stillingsprosent> call() throws StillingsprosentCallableException {
        List<Stillingsprosent> stillingsprosenter;
        try {
            stillingsprosenter = endpointRouter.getStillingsprosenter(fnr, tpOrdning, tpLeverandor);
        } catch (Exception e) {
            e.printStackTrace();
            StillingsprosentCallableException ex = new StillingsprosentCallableException("Call to getStillingsprosenter failed with exception: " + e.toString(), e, tpOrdning);
            LOG.warn("Rethrowing as: {}", ex.toString());
            throw ex;
        }
        return stillingsprosenter;
    }
}
