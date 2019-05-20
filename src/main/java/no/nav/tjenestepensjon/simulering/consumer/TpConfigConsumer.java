package no.nav.tjenestepensjon.simulering.consumer;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public interface TpConfigConsumer {
    String findTpLeverandor(TPOrdning tpOrdning);
}
