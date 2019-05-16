package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.List;
import java.util.Random;
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
    private final TjenestepensjonSimuleringMetrics metrics;

    public StillingsprosentCallable(TPOrdning tpOrdning, String fnr, String simuleringsKode, TjenestepensjonSimuleringMetrics metrics) {
        this.tpOrdning = tpOrdning;
        this.fnr = fnr;
        this.simuleringsKode = simuleringsKode;
        this.metrics = metrics;
    }

    @Override
    public List<Stillingsprosent> call() throws GenericStillingsprosentCallableException {
        metrics.incrementCounter(tpOrdning.getTpId(), TP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting stillingsprosent from: {}", Thread.currentThread().getName(), tpOrdning.getTpId());

        //TODO implement soap-call and throw GenericStillingsprosentCallableException on timeout or other errors
        try {
            Thread.sleep(new Random().nextInt(3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpOrdning.getTpId(), TP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        LOG.info("Retrieved stillingsprosent from: {} in: {} ms", tpOrdning.getTpId(), elapsed);

        Stillingsprosent stillingsprosent = new Stillingsprosent();
        stillingsprosent.setStillingsprosent(new Random().nextDouble());
        return List.of(stillingsprosent);
    }
}
