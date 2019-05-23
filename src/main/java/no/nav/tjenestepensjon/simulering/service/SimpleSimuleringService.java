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

    private final StillingsprosentService stillingsprosentService;
    private final TpConfigConsumer tpConfigConsumer;
    private final List<TpLeverandor> tpLeverandorList;
    private final TpRegisterConsumer tpRegisterConsumer;
    private final AsyncExecutor<TpLeverandor, FindTpLeverandorCallable> asyncExecutor;

    public SimpleSimuleringService(StillingsprosentService stillingsprosentService, TpConfigConsumer tpConfigConsumer,
            List<TpLeverandor> tpLeverandorList, TpRegisterConsumer tpRegisterConsumer,
            AsyncExecutor<TpLeverandor, FindTpLeverandorCallable> asyncExecutor) {
        this.stillingsprosentService = stillingsprosentService;
        this.tpConfigConsumer = tpConfigConsumer;
        this.tpLeverandorList = tpLeverandorList;
        this.tpRegisterConsumer = tpRegisterConsumer;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public OutgoingResponse simuler(IncomingRequest request) {
        OutgoingResponse response = createEmpyResponse();
        try {
            List<TPOrdning> tpOrdningList = getTpOrdningerAndLeverandor(request.getFnr());
            StillingsprosentResponse stillingsprosentResponse = stillingsprosentService.getStillingsprosentListe(request.getFnr(), tpOrdningList);
            handleStillingsprosentExceptions(response, stillingsprosentResponse);
            TPOrdning latest = stillingsprosentService.getLatestFromStillingsprosent(stillingsprosentResponse.getTpOrdningListMap());
        } catch (DuplicateStillingsprosentEndDateException e) {
            SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
            simulertPensjon.setStatus("FEIL");
            simulertPensjon.setFeilkode("PARF");
            return response;
        } catch (MissingStillingsprosentException e) {
            SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
            simulertPensjon.setStatus("FEIL");
            simulertPensjon.setFeilkode("IKKE");
            return response;
        } catch (NoTpOrdningerFoundException e) {
            return new OutgoingResponse();
        }

        return response;
    }

    private void handleStillingsprosentExceptions(OutgoingResponse response, StillingsprosentResponse stillingsprosentResponse) {
        response.getSimulertPensjonListe().get(0).setUtelatteTpnr(stillingsprosentResponse.getExceptions().stream()
                .filter(e -> e.getCause() instanceof StillingsprosentCallableException)
                .map(e -> ((StillingsprosentCallableException) e.getCause()).getTpOrdning().getTpId()).collect(Collectors.toList()));
        if (stillingsprosentResponse.getExceptions().size() > 0) {
            stillingsprosentResponse.getExceptions().forEach(e -> LOG.error(e.toString()));
        }
        if (stillingsprosentResponse.getTpOrdningListMap().size() == 0) {
            throw new NullPointerException("Could not get response fom any TP-Providers");
        }
    }

    private OutgoingResponse createEmpyResponse() {
        OutgoingResponse response = new OutgoingResponse();
        SimulertPensjon simulertPensjon = new SimulertPensjon();
        response.setSimulertPensjonListe(List.of(simulertPensjon));
        return response;
    }

    private List<TPOrdning> getTpOrdningerAndLeverandor(String fnr) throws NoTpOrdningerFoundException {
        List<TPOrdning> tpOrdningList = tpRegisterConsumer.getTpOrdningerForPerson(fnr);
        Map<TPOrdning, FindTpLeverandorCallable> callableMap = new HashMap<>();
        tpOrdningList.forEach(tpOrdning -> callableMap.put(tpOrdning, new FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorList)));
        AsyncResponse<TPOrdning, TpLeverandor> asyncResponse = asyncExecutor.executeAsync(callableMap);
        tpOrdningList.forEach(tpOrdning -> tpOrdning.setTpLeverandor(asyncResponse.getResultMap().get(tpOrdning)));
        LOG.info("GOT TP ORDNINGER {} FOR USER {}", tpOrdningList.toString(), fnr);
        return tpOrdningList;
    }
}
