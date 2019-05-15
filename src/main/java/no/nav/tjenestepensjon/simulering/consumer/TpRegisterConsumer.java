package no.nav.tjenestepensjon.simulering.consumer;

import java.util.List;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public interface TpRegisterConsumer {

    List<TPOrdning> getTpOrdningerForPerson(String fnr);
}
