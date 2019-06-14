package no.nav.tjenestepensjon.simulering.service;

import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.tjenestepensjon.simulering.AppMetrics;
import no.nav.tjenestepensjon.simulering.AsyncExecutor;
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse;
import no.nav.tjenestepensjon.simulering.StillingsprosentCallable;
import no.nav.tjenestepensjon.simulering.TjenestepensjonsimuleringEndpointRouter;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;

@Component
public class StillingsprosentServiceImpl implements StillingsprosentService {

    private static final Logger LOG = LoggerFactory.getLogger(Stillingsprosent.class);
    private final AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable> asyncExecutor;
    private final TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter;
    private final AppMetrics metrics;

    public StillingsprosentServiceImpl(AsyncExecutor<List<Stillingsprosent>, StillingsprosentCallable> asyncExecutor,
            TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter,
            AppMetrics metrics) {
        this.asyncExecutor = asyncExecutor;
        this.simuleringEndPointRouter = simuleringEndPointRouter;
        this.metrics = metrics;
    }

    @Override
    public StillingsprosentResponse getStillingsprosentListe(String fnr, Map<TPOrdning, TpLeverandor> tpOrdningAndLeverandorMap) {
        Map<TPOrdning, StillingsprosentCallable> callableMap = toCallableMap(fnr, tpOrdningAndLeverandorMap);
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
                LOG.info("TPORDNING {} STILLINGSPROSENT {}", entry.getKey(), stillingsprosent);
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
            throw new DuplicateStillingsprosentEndDateException("Could not decide latest stillingprosent due to multiple stillingsprosent having the same end date");
        } else if (latest.getDatoTom() == null || latest.getDatoTom().isAfter(other.getDatoTom())) {
            return latest;
        } else if (other.getDatoTom() == null || other.getDatoTom().isAfter(latest.getDatoTom())) {
            return other;
        } else {
            return latest;
        }
    }

    private Map<TPOrdning, StillingsprosentCallable> toCallableMap(String fnr, Map<TPOrdning, TpLeverandor> tpOrdningAndLeverandorMap) {
        Map<TPOrdning, StillingsprosentCallable> callableMap = new HashMap<>();
        tpOrdningAndLeverandorMap.keySet()
                .forEach(tpOrdning -> callableMap.put(tpOrdning, new StillingsprosentCallable(fnr, tpOrdning, tpOrdningAndLeverandorMap.get(tpOrdning), simuleringEndPointRouter)));
        return callableMap;
    }
}
