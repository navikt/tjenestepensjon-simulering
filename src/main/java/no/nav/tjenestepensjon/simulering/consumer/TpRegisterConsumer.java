package no.nav.tjenestepensjon.simulering.consumer;

import java.util.List;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

public interface TpRegisterConsumer {

    List<TPOrdning> getTpOrdningerForPerson(String fnr) throws NoTpOrdningerFoundException;
}
