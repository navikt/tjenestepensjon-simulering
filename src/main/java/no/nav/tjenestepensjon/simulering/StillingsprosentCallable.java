package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.List;
import java.util.concurrent.Callable;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class StillingsprosentCallable implements Callable {

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
    public Object call() {
        metrics.incrementCounter(tpOrdning.getTpId(), TP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        LOG.info("{} getting stillingsprosenter from: {}", Thread.currentThread().getName(), tpOrdning.getTpId());

        List<Stillingsprosent> stillingsprosenter = simulering.getStillingsprosenter();

        long elapsed = metrics.elapsedSince(startTime);
        metrics.incrementCounter(tpOrdning.getTpId(), TP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        LOG.info("Retrieved stillingsprosenter from: {} in: {} ms", tpOrdning.getTpId(), elapsed);
        return stillingsprosenter;
    }
}
