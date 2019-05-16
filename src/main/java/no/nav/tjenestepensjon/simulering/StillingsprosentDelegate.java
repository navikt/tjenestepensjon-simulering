package no.nav.tjenestepensjon.simulering;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Component
public class StillingsprosentDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StillingsprosentDelegate.class);
    private final AsyncExecutor<StillingsprosentCallable, Stillingsprosent> asyncExecutor;
    private final Tjenestepensjonsimulering simulering;
    private final TjenestepensjonSimuleringMetrics metrics;

    public StillingsprosentDelegate(AsyncExecutor<StillingsprosentCallable, Stillingsprosent> asyncExecutor,
                                    Tjenestepensjonsimulering simulering,
                                    TjenestepensjonSimuleringMetrics metrics) {
        this.asyncExecutor = asyncExecutor;
        this.simulering = simulering;
        this.metrics = metrics;
    }

    public List<Stillingsprosent> findStillingsprosenter(List<TPOrdning> tpOrdninger, String fnr, String simuleringsKode) throws ExecutionException, InterruptedException {
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS);
        final long startTime = metrics.startTime();
        final List<Stillingsprosent> stillingsprosenter = asyncExecutor.executeAsync(toCallables(tpOrdninger, fnr, simuleringsKode));
        final long elapsed = metrics.elapsedSince(startTime);
        LOG.info("Retrieved all stillingsprosenter in: {} ms", elapsed);
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        return stillingsprosenter;
    }

    private List<StillingsprosentCallable> toCallables(List<TPOrdning> tpOrdninger, String fnr, String simuleringsKode) {
        return tpOrdninger.stream().map(tpOrdning -> new StillingsprosentCallable(tpOrdning, fnr, simuleringsKode, simulering, metrics)).collect(Collectors.toList());
    }
}
