package no.nav.tjenestepensjon.simulering;

import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class StillingsprosentCallable implements Callable {

    private static final Logger LOG = LoggerFactory.getLogger(StillingsprosentCallable.class);
    private final TPOrdning tpOrdning;
    private final String fnr;
    private final String simuleringsKode;

    public StillingsprosentCallable(TPOrdning tpOrdning, String fnr, String simuleringsKode) {
        this.tpOrdning = tpOrdning;
        this.fnr = fnr;
        this.simuleringsKode = simuleringsKode;
    }

    @Override
    public Object call() throws Exception {
        long startTime = System.currentTimeMillis();
        LOG.info("{} getting stillingsprosent from: {}", Thread.currentThread().getName(), tpOrdning.getTpId());

        //TODO implement soap-call
        Thread.sleep(2500);

        long elapsed = System.currentTimeMillis() - startTime;
        LOG.info("Retrieved stillingsprosent from: {} in: {} ms", tpOrdning.getTpId(), elapsed);
        return new Random().nextInt();
    }
}
