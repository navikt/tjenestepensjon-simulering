package no.nav.tjenestepensjon.simulering.service;

import java.util.List;
import java.util.Map;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;

public interface StillingsprosentService {

    StillingsprosentResponse getStillingsprosentListe(String fnr, Map<TPOrdning, TpLeverandor> tpOrdningAndLeverandorMap);
    TPOrdning getLatestFromStillingsprosent(Map<TPOrdning, List<Stillingsprosent>> map) throws DuplicateStillingsprosentEndDateException, MissingStillingsprosentException;
}
