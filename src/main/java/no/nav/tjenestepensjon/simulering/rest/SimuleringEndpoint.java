package no.nav.tjenestepensjon.simulering.rest;

import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_SIMULERING_CALLS;
import static no.nav.tjenestepensjon.simulering.util.Utils.addHeaderToRequestContext;
import static no.nav.tjenestepensjon.simulering.util.Utils.getHeaderFromRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics;

@RestController
public class SimuleringEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(SimuleringEndpoint.class);
    private final SimuleringService service;
    private final TjenestepensjonSimuleringMetrics metrics;

    public SimuleringEndpoint(SimuleringService service, TjenestepensjonSimuleringMetrics metrics) {
        this.service = service;
        this.metrics = metrics;
    }

    @RequestMapping(value = "/simulering", method = RequestMethod.GET)
    public ResponseEntity<OutgoingResponse> simuler(@RequestBody IncomingRequest request, @RequestHeader(value = "nav-call-id", required = false) String navCallId) {
        addHeaderToRequestContext("nav-call-id", navCallId);
        LOG.info("Processing nav-call-id: {}, request: {}", getHeaderFromRequestContext("nav-call-id"), request.toString());
        metrics.incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_CALLS);
        OutgoingResponse response = service.simuler(request);
        LOG.info("Processing nav-call-id: {}, response: {}", getHeaderFromRequestContext("nav-call-id"), response.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public interface SimuleringService {
        OutgoingResponse simuler(IncomingRequest simuler);
    }
}
