package no.nav.tjenestepensjon.simulering.service;

import java.util.List;
import java.util.Map;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

public interface StillingsprosentService {

    StillingsprosentResponse getStillingsprosentListe(String fnr) throws NoTpOrdningerFoundException;
    TPOrdning getLatestFromStillingsprosent(Map<TPOrdning, List<Stillingsprosent>> map) throws DuplicateStillingsprosentEndDateException, MissingStillingsprosentException;
}
