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
    private final String fnr;
    private final TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter;
    private final TjenestepensjonSimuleringMetrics metrics;

    public StillingsprosentCallable(String fnr, TPOrdning tpOrdning,
                                    TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter,
            TjenestepensjonSimuleringMetrics metrics) {
        this.tpOrdning = tpOrdning;
        this.fnr = fnr;
        this.simuleringEndPointRouter = simuleringEndPointRouter;
        this.metrics = metrics;
    }

    @Override
    public List<Stillingsprosent> call() throws StillingsprosentCallableException {
        TpLeverandor tpLeverandor = tpOrdning.getTpLeverandor();
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().getName(), tpLeverandor);

        List<Stillingsprosent> stillingsprosenter;
        try {
            stillingsprosenter = simuleringEndPointRouter.getStillingsprosenter(fnr, tpOrdning);
        } catch (Exception e) {
            LOG.warn(e.toString());
            throw new StillingsprosentCallableException("Call to getStillingsprosenter failed: " + e.getMessage(), e, tpOrdning);
        }

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpLeverandor.getName(), TP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpLeverandor, elapsed);
        return stillingsprosenter;
    }
}
