package no.nav.tjenestepensjon.simulering;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;

import java.util.List;

public interface Tjenestepensjonsimulering {

    List<Stillingsprosent> getStillingsprosenter();
}
