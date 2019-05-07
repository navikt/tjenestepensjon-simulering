package no.nav.tjenestepensjon.simulering;

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

    private static final Logger LOG = LoggerFactory.getLogger(Stillingsprosent.class);
    private final AsyncExecutor<StillingsprosentCallable, Stillingsprosent> asyncExecutor;

    public StillingsprosentDelegate(AsyncExecutor<StillingsprosentCallable, Stillingsprosent> asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public List<Stillingsprosent> findStillingsprosenter(List<TPOrdning> tpOrdninger, String fnr, String simuleringsKode) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        List<Stillingsprosent> stillingsprosentList = asyncExecutor.executeAsync(toCallables(tpOrdninger, fnr, simuleringsKode));
        long elapsed = System.currentTimeMillis() - startTime;
        LOG.info("Retrieved all stillingsprosenter in: {} ms", elapsed);
        return stillingsprosentList;
    }

    private static List<StillingsprosentCallable> toCallables(List<TPOrdning> tpOrdninger, String fnr, String simuleringsKode) {
        return tpOrdninger.stream().map(tpOrdning -> new StillingsprosentCallable(tpOrdning, fnr, simuleringsKode)).collect(Collectors.toList());
    }
}
