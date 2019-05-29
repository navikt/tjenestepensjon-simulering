package no.nav.tjenestepensjon.simulering.service;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

import java.util.List;

public interface SimulerPensjonService {

    SimulerPensjonResponse simulerPensjon(List<TPOrdning> tpOrdningList, TPOrdning tpLatest);
}
