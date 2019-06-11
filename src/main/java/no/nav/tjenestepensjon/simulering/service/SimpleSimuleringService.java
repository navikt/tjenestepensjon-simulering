package no.nav.tjenestepensjon.simulering.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public SimpleSimuleringService(TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter, StillingsprosentService stillingsprosentService,
            TpConfigConsumer tpConfigConsumer,
            List<TpLeverandor> tpLeverandorList, TpRegisterConsumer tpRegisterConsumer,
            AsyncExecutor<TpLeverandor, FindTpLeverandorCallable> asyncExecutor) {
        this.simuleringEndPointRouter = simuleringEndPointRouter;
        this.stillingsprosentService = stillingsprosentService;
        this.tpConfigConsumer = tpConfigConsumer;
        this.tpLeverandorList = tpLeverandorList;
        this.tpRegisterConsumer = tpRegisterConsumer;
        this.asyncExecutor = asyncExecutor;
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
                throw new NullPointerException("Could not get response fom any TP-Providers");
            }

            TPOrdning tpOrdningLatest = stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.getTpOrdningStillingsprosentMap());
            List<SimulertPensjon> simulertPensjonList = simuleringEndPointRouter
                    .simulerPensjon(request, tpOrdningLatest, tpOrdningAndLeverandorMap.get(tpOrdningLatest), stillingsprosentResponse.getTpOrdningStillingsprosentMap());
            response.setSimulertPensjonListe(addTpOrdningInfo(simulertPensjonList, stillingsprosentResponse));
        } catch (DuplicateStillingsprosentEndDateException e) {
            response.setSimulertPensjonListe(addResponseInfoWhenException(List.of(new SimulertPensjon()), stillingsprosentResponse, "PARF", e));
        } catch (MissingStillingsprosentException e) {
            response.setSimulertPensjonListe(addResponseInfoWhenException(List.of(new SimulertPensjon()), stillingsprosentResponse, "IKKE", e));
        } catch (NoTpOrdningerFoundException e) {
            return new OutgoingResponse();
        }

        return response;
    }

    private List<SimulertPensjon> addResponseInfoWhenException(List<SimulertPensjon> simulertPensjonList, StillingsprosentResponse stillingsprosentResponse, String feilKode,
            Exception e) {
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setFeilkode(feilKode));
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setFeilbeskrivelse(e.getMessage()));
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setStatus("FEIL"));
        addTpOrdningInfo(simulertPensjonList, stillingsprosentResponse);
        return simulertPensjonList;
    }

    private List<SimulertPensjon> addTpOrdningInfo(List<SimulertPensjon> simulertPensjonList, StillingsprosentResponse stillingsprosentResponse) {
        List<String> utelatteTpOrdninger = stillingsprosentResponse.getExceptions().stream()
                .filter(e -> e.getCause() instanceof StillingsprosentCallableException)
                .map(e -> ((StillingsprosentCallableException) e.getCause()).getTpOrdning().getTpId()).collect(Collectors.toList());
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setUtelatteTpnr(utelatteTpOrdninger));
        simulertPensjonList.forEach(simulertPensjon -> simulertPensjon.setInkluderteTpnr(stillingsprosentResponse.getTpOrdningStillingsprosentMap().keySet().stream()
                .map(TPOrdning::getTpId).collect(Collectors.toList())));
        return simulertPensjonList;
    }

    private Map<TPOrdning, TpLeverandor> getTpLeverandorer(List<TPOrdning> tpOrdningList) {
        Map<TPOrdning, FindTpLeverandorCallable> callableMap = new HashMap<>();
        tpOrdningList.forEach(tpOrdning -> callableMap.put(tpOrdning, new FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorList)));
        AsyncResponse<TPOrdning, TpLeverandor> asyncResponse = asyncExecutor.executeAsync(callableMap);
        return asyncResponse.getResultMap();
    }
}
