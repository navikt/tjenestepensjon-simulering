package no.nav.tjenestepensjon.simulering.service;

import static java.util.stream.Collectors.toList;

import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_FEIL;
import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL;
import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_OK;
import static no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import no.nav.tjenestepensjon.simulering.AppMetrics;
import no.nav.tjenestepensjon.simulering.AsyncExecutor;
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse;
import no.nav.tjenestepensjon.simulering.TjenestepensjonsimuleringEndpointRouter;
import no.nav.tjenestepensjon.simulering.consumer.FindTpLeverandorCallable;
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer;
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;
import no.nav.tjenestepensjon.simulering.exceptions.SoapFaultException;
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;
import no.nav.tjenestepensjon.simulering.rest.SimuleringEndpoint;

@Service
public class SimpleSimuleringService implements SimuleringEndpoint.SimuleringService {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleSimuleringService.class);

    private final TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter;
    private final StillingsprosentService stillingsprosentService;
    private final TpConfigConsumer tpConfigConsumer;
    private final List<TpLeverandor> tpLeverandorList;
    private final TpRegisterConsumer tpRegisterConsumer;
    private final AsyncExecutor<TpLeverandor, FindTpLeverandorCallable> asyncExecutor;
    private final AppMetrics metrics;

    public SimpleSimuleringService(TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter, StillingsprosentService stillingsprosentService,
            TpConfigConsumer tpConfigConsumer,
            List<TpLeverandor> tpLeverandorList, TpRegisterConsumer tpRegisterConsumer,
            AsyncExecutor<TpLeverandor, FindTpLeverandorCallable> asyncExecutor, AppMetrics metrics) {
        this.simuleringEndPointRouter = simuleringEndPointRouter;
        this.stillingsprosentService = stillingsprosentService;
        this.tpConfigConsumer = tpConfigConsumer;
        this.tpLeverandorList = tpLeverandorList;
        this.tpRegisterConsumer = tpRegisterConsumer;
        this.asyncExecutor = asyncExecutor;
        this.metrics = metrics;
    }

    @Override
    public OutgoingResponse simuler(IncomingRequest request) {
        OutgoingResponse response = new OutgoingResponse();
        StillingsprosentResponse stillingsprosentResponse = null;
        try {
            List<TPOrdning> tpOrdningList = tpRegisterConsumer.getTpOrdningerForPerson(request.getFnr());
            Map<TPOrdning, TpLeverandor> tpOrdningAndLeverandorMap = getTpLeverandorer(tpOrdningList);
            stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(request.getFnr(), tpOrdningAndLeverandorMap);

            if (stillingsprosentResponse.getTpOrdningStillingsprosentMap().size() == 0) {
                response.setSimulertPensjonListe(addResponseInfoWhenError("", "Could not get stillingsprosent from any TP-Providers"));
                return response;
            }

            TPOrdning tpOrdning = stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.getTpOrdningStillingsprosentMap());
            TpLeverandor tpLeverandor = tpOrdningAndLeverandorMap.get(tpOrdning);
            List<SimulertPensjon> simulertPensjonList =
                    simuleringEndPointRouter.simulerPensjon(request, tpOrdning, tpLeverandor, stillingsprosentResponse.getTpOrdningStillingsprosentMap());
            response.setSimulertPensjonListe(addResponseInfoWhenSimulert(simulertPensjonList, stillingsprosentResponse));
        } catch (DuplicateStillingsprosentEndDateException e) {
            response.setSimulertPensjonListe(addResponseInfoWhenError("PARF", e.getMessage()));
        } catch (MissingStillingsprosentException | SoapFaultException e) {
            response.setSimulertPensjonListe(addResponseInfoWhenError("IKKE", e.getMessage()));
        } catch (NoTpOrdningerFoundException e) {
            response.setSimulertPensjonListe(addResponseInfoWhenError("", e.getMessage()));
        }
        return response;
    }

    private List<SimulertPensjon> addResponseInfoWhenError(String feilKode, String msg) {
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_FEIL);
        List<SimulertPensjon> simulertPensjonList = List.of(new SimulertPensjon());
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setFeilkode(feilKode));
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setFeilbeskrivelse(msg));
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setStatus("FEIL"));
        return simulertPensjonList;
    }

    private List<SimulertPensjon> addResponseInfoWhenSimulert(List<SimulertPensjon> simulertPensjonList, StillingsprosentResponse stillingsprosentResponse) {
        List<String> utelatteTpNr = stillingsprosentResponse.getExceptions().stream()
                .filter(e -> e.getCause() instanceof StillingsprosentCallableException)
                .map(e -> ((StillingsprosentCallableException) e.getCause()).getTpOrdning().getTpId())
                .collect(toList());
        List<String> inkluderteTpNr = stillingsprosentResponse.getTpOrdningStillingsprosentMap().keySet().stream()
                .map(TPOrdning::getTpId)
                .collect(toList());
        simulertPensjonList.forEach(simulertPensjon -> {
            simulertPensjon.setUtelatteTpnr(utelatteTpNr);
            simulertPensjon.setInkluderteTpnr(inkluderteTpNr);
            if (utelatteTpNr.size() > 0) {
                simulertPensjon.setStatus("UFUL");
            }
        });
        incrementMetrics(simulertPensjonList, utelatteTpNr);
        return simulertPensjonList;
    }

    private void incrementMetrics(List<SimulertPensjon> simulertPensjonList, List<String> utelatteTpNr) {
        boolean ufullstendig = utelatteTpNr.size() > 0;
        boolean mangelfull = simulertPensjonList.stream()
                .anyMatch(simulertPensjon -> simulertPensjon.getUtbetalingsperioder().stream()
                        .anyMatch(utbetalingsperiode -> utbetalingsperiode.getMangelfullSimuleringkode() != null));

        if (ufullstendig) {
            metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL);
        }
        if (mangelfull) {
            metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL);
        }
        if (!ufullstendig && !mangelfull) {
            metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_OK);
        }
    }

    private Map<TPOrdning, TpLeverandor> getTpLeverandorer(List<TPOrdning> tpOrdningList) {
        Map<TPOrdning, FindTpLeverandorCallable> callableMap = new HashMap<>();
        tpOrdningList.forEach(tpOrdning -> callableMap.put(tpOrdning, new FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorList)));
        AsyncResponse<TPOrdning, TpLeverandor> asyncResponse = asyncExecutor.executeAsync(callableMap);
        return asyncResponse.getResultMap();
    }
}
