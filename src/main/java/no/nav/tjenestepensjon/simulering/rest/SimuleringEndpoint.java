package no.nav.tjenestepensjon.simulering.rest;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import no.nav.tjenestepensjon.simulering.StillingsprosentDelegate;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@RestController
public class SimuleringEndpoint {

    private final SimuleringService service;
    private final StillingsprosentDelegate stillingsprosentDelegate;

    public SimuleringEndpoint(SimuleringService service, StillingsprosentDelegate stillingsprosentDelegate) {
        this.service = service;
        this.stillingsprosentDelegate = stillingsprosentDelegate;
    }

    @RequestMapping(value = "simulering", method = RequestMethod.GET)
    public ResponseEntity<OutgoingResponse> simuler(@RequestBody IncomingRequest request) throws ExecutionException, InterruptedException {
        List<TPOrdning> tpOrdninger = List.of(new TPOrdning("8000123", "123")); //TODO get tp-ordninger
        List<Stillingsprosent> stillingsprosenter = stillingsprosentDelegate.findStillingsprosenter(tpOrdninger, "fnr", "kode");
        return new ResponseEntity<>(service.simuler(request), HttpStatus.OK);
    }

    public interface SimuleringService {
        OutgoingResponse simuler(IncomingRequest simuler);
    }
}
