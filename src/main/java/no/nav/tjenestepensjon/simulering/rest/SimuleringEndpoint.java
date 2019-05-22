package no.nav.tjenestepensjon.simulering.rest;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics;
import no.nav.tjenestepensjon.simulering.consumer.TokenServiceConsumer;

@RestController
public class SimuleringEndpoint {

    private final SimuleringService service;
    private final TjenestepensjonSimuleringMetrics metrics;
    private final TokenServiceConsumer tokenServiceConsumer;

    public SimuleringEndpoint(SimuleringService service, TjenestepensjonSimuleringMetrics metrics,
            TokenServiceConsumer tokenServiceConsumer) {
        this.service = service;
        this.metrics = metrics;
        this.tokenServiceConsumer = tokenServiceConsumer;
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
    public ResponseEntity async(@PathVariable("fnr") String fnr) {
        IncomingRequest request = new IncomingRequest();
        request.setFnr(fnr);
        return ResponseEntity.ok(service.simuler(request));
    }
}
