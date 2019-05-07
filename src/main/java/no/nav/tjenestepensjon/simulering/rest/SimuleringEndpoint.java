package no.nav.tjenestepensjon.simulering.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SimuleringEndpoint {

    private SimuleringService service;

    public SimuleringEndpoint(SimuleringService service) {
        this.service = service;
    }

    @RequestMapping(value = "simulering", method = RequestMethod.GET)
    public ResponseEntity<OutgoingResponse> simuler(@RequestBody IncomingRequest request){
        return new ResponseEntity<>(service.simuler(request), HttpStatus.OK);
    }

    public interface SimuleringService{
        OutgoingResponse simuler(IncomingRequest simuler);
    }
}
