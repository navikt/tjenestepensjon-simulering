package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.GenericStillingsprosentCallableException;

public class StillingsprosentCallable implements Callable<List<Stillingsprosent>> {

    private static final Logger LOG = LoggerFactory.getLogger(StillingsprosentCallable.class);
    private final TPOrdning tpOrdning;
    private final String fnr;
    private final String simuleringsKode;
    private final Tjenestepensjonsimulering simulering;
    private final TjenestepensjonSimuleringMetrics metrics;

    public StillingsprosentCallable(TPOrdning tpOrdning,
                                    String fnr,
                                    String simuleringsKode,
                                    Tjenestepensjonsimulering simulering,
                                    TjenestepensjonSimuleringMetrics metrics) {
        this.tpOrdning = tpOrdning;
        this.fnr = fnr;
        this.simuleringsKode = simuleringsKode;
        this.simulering = simulering;
        this.metrics = metrics;
    }

    @Override
    public List<Stillingsprosent> call() throws GenericStillingsprosentCallableException {
        String tpId = tpOrdning.getTpId();
        metrics.incrementCounter(tpId, TP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().getName(), tpId);

        List<Stillingsprosent> stillingsprosenter = simulering.getStillingsprosenter(
            fnr, tpId, tpOrdning.getTssId(), simuleringsKode);

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpId, TP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpId, elapsed);
        return stillingsprosenter;
    }
}
