package no.nav.tjenestepensjon.simulering.consumer;

import java.util.List;
import java.util.concurrent.Callable;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

public class FindTpLeverandorCallable implements Callable<TpLeverandor> {

    private final TPOrdning tpOrdning;
    private final TpConfigConsumer tpConfigConsumer;
    private final List<TpLeverandor> tpLeverandorList;

    public FindTpLeverandorCallable(TPOrdning tpOrdning, TpConfigConsumer tpConfigConsumer, List<TpLeverandor> tpLeverandorList) {
        this.tpOrdning = tpOrdning;
        this.tpConfigConsumer = tpConfigConsumer;
        this.tpLeverandorList = tpLeverandorList;
    }

    @Override
    public TpLeverandor call() throws Exception {
        String tpLeverandor = tpConfigConsumer.findTpLeverandor(tpOrdning);
        return tpLeverandorList.stream().filter(l -> tpLeverandor.equalsIgnoreCase(l.getName())).findFirst().get();
    }
}
