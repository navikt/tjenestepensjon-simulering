package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;

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
    private final TjenestepensjonSimuleringMetrics metrics;

    public StillingsprosentCallable(String fnr, TPOrdning tpOrdning,
            TpLeverandor tpLeverandor, TjenestepensjonsimuleringEndpointRouter endpointRouter,
            TjenestepensjonSimuleringMetrics metrics) {
        this.tpOrdning = tpOrdning;
        this.fnr = fnr;
        this.tpLeverandor = tpLeverandor;
        this.endpointRouter = endpointRouter;
        this.metrics = metrics;
    }

    @Override
    public List<Stillingsprosent> call() throws StillingsprosentCallableException {
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().getName(), tpLeverandor);

        List<Stillingsprosent> stillingsprosenter;
        try {
            stillingsprosenter = endpointRouter.getStillingsprosenter(fnr, tpOrdning, tpLeverandor);
        } catch (Exception e) {
            StillingsprosentCallableException ex = new StillingsprosentCallableException("Call to getStillingsprosenter failed: " + e.getMessage(), e, tpOrdning);
            LOG.warn(ex.toString());
            throw ex;
        }

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpLeverandor, elapsed);
        return stillingsprosenter;
    }
}
