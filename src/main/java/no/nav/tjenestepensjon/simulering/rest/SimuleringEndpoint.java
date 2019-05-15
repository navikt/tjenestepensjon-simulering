package no.nav.tjenestepensjon.simulering.rest;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.tjenestepensjon.simulering.StillingsprosentDelegate;
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics;
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@RestController
public class SimuleringEndpoint {

    private final SimuleringService service;
    private final StillingsprosentDelegate stillingsprosentDelegate;
    private final List<TPOrdning> tpOrdningList;
    private final TjenestepensjonSimuleringMetrics metrics;
    private final TpRegisterConsumer tpRegisterConsumer;

    public SimuleringEndpoint(SimuleringService service, StillingsprosentDelegate stillingsprosentDelegate, List<TPOrdning> tpOrdningList,
            TjenestepensjonSimuleringMetrics metrics, TpRegisterConsumer tpRegisterConsumer) {
        this.service = service;
        this.stillingsprosentDelegate = stillingsprosentDelegate;
        this.tpOrdningList = tpOrdningList;
        this.metrics = metrics;
        this.tpRegisterConsumer = tpRegisterConsumer;
        //TODO refactor
    }

    @RequestMapping(value = "simulering", method = RequestMethod.GET)
    public ResponseEntity<OutgoingResponse> simuler(@RequestBody IncomingRequest request) {
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_CALLS);
        return new ResponseEntity<>(service.simuler(request), HttpStatus.OK);
    }

    public interface SimuleringService {
        OutgoingResponse simuler(IncomingRequest simuler);
    }

    @GetMapping("/async/{fnr}")
    public ResponseEntity async(@PathVariable("fnr") String fnr) throws ExecutionException, InterruptedException {
        List<TPOrdning> tpOrdningerForPerson = tpRegisterConsumer.getTpOrdningerForPerson(fnr);
        return ResponseEntity.ok(stillingsprosentDelegate.findStillingsprosenter(tpOrdningList, fnr, "kode"));
    }
}
