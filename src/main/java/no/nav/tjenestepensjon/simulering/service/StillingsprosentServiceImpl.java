package no.nav.tjenestepensjon.simulering.service;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.tjenestepensjon.simulering.AsyncExecutor;
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse;
import no.nav.tjenestepensjon.simulering.StillingsprosentCallable;
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics;
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

@Component
public class StillingsprosentServiceImpl implements StillingsprosentService {

    private static final Logger LOG = LoggerFactory.getLogger(Stillingsprosent.class);
    private final AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable> asyncExecutor;
    private final Tjenestepensjonsimulering simulering;
    private final TjenestepensjonSimuleringMetrics metrics;
    private final TpRegisterConsumer tpRegisterConsumer;

    public StillingsprosentServiceImpl(AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable> asyncExecutor,
                                       Tjenestepensjonsimulering simulering,
                                       TjenestepensjonSimuleringMetrics metrics,
                                       TpRegisterConsumer tpRegisterConsumer) {
        this.asyncExecutor = asyncExecutor;
        this.simulering = simulering;
        this.metrics = metrics;
        this.tpRegisterConsumer = tpRegisterConsumer;
    }

    @Override
    public StillingsprosentResponse getStillingsprosentListe(String fnr) throws NoTpOrdningerFoundException {
        List<TPOrdning> tpOrdningerForPerson = tpRegisterConsumer.getTpOrdningerForPerson(fnr);
        return retriveFromProviders(fnr, "kode", tpOrdningerForPerson);
    }

    private StillingsprosentResponse retriveFromProviders(String fnr, String simuleringsKode, List<TPOrdning> tpOrdninger) {
        Map<TPOrdning, StillingsprosentCallable> callableMap = toCallableMap(tpOrdninger, fnr, simuleringsKode);
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_CALLS);
        long startTime = metrics.startTime();
        AsyncResponse<TPOrdning, List<Stillingsprosent>> asyncResponse = asyncExecutor.executeAsync(callableMap);
        long elapsed = metrics.elapsedSince(startTime);
        LOG.info("Retrieved all stillingsprosenter in: {} ms", elapsed);
        metrics.incrementCounter(APP_NAME, APP_TOTAL_STILLINGSPROSENT_TIME, elapsed);
        return new StillingsprosentResponse(asyncResponse.getResultMap(), asyncResponse.getExceptions());
    }

    @Override
    public TPOrdning getLatestFromStillingsprosent(Map<TPOrdning, List<Stillingsprosent>> map) throws DuplicateStillingsprosentEndDateException, MissingStillingsprosentException {
        TPOrdning latestOrdning = null;
        Stillingsprosent latestPct = null;
        for (Map.Entry<TPOrdning, List<Stillingsprosent>> entry : map.entrySet()) {
            for (Stillingsprosent stillingsprosent : entry.getValue()) {
                if (latestOrdning == null) {
                    latestOrdning = entry.getKey();
                    latestPct = stillingsprosent;
                } else {
                    Stillingsprosent pct = getLatest(latestPct, stillingsprosent);
                    if (!pct.equals(latestPct)) {
                        latestOrdning = entry.getKey();
                        latestPct = stillingsprosent;
                    }
                }
            }
        }
        if (latestPct == null) {
            throw new MissingStillingsprosentException("Could not find any stillingsprosent");
        }
        return latestOrdning;
    }

    private Stillingsprosent getLatest(Stillingsprosent latest, Stillingsprosent other) throws DuplicateStillingsprosentEndDateException {
        if (latest.getDatoTom() == null && other.getDatoTom() != null) {
            return latest;
        } else if (latest.getDatoTom() != null && other.getDatoTom() == null) {
            return other;
        } else if (latest.getDatoTom() == null && other.getDatoTom() == null || latest.getDatoTom().isEqual(other.getDatoTom())) {
            throw new DuplicateStillingsprosentEndDateException("Stillingsprosent");
        } else if (latest.getDatoTom() == null || latest.getDatoTom().isAfter(other.getDatoTom())) {
            return latest;
        } else if (other.getDatoTom() == null || other.getDatoTom().isAfter(latest.getDatoTom())) {
            return other;
        } else {
            return latest;
        }
    }

    private Map<TPOrdning, StillingsprosentCallable> toCallableMap(List<TPOrdning> tpOrdninger, String fnr, String simuleringsKode) {
        Map<TPOrdning, StillingsprosentCallable> callableMap = new HashMap<>();
        tpOrdninger.forEach(tpOrdning -> callableMap.put(tpOrdning, new StillingsprosentCallable(tpOrdning, fnr, simuleringsKode, simulering, metrics)));
        return callableMap;
    }
}
