package no.nav.tjenestepensjon.simulering;

import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.SimuleringEndpoint;
import org.springframework.stereotype.Service;

@Service
public class SimpleSimuleringService implements SimuleringEndpoint.SimuleringService {

    @Override
    public OutgoingResponse simuler(IncomingRequest simuler) {
        return new OutgoingResponse();
    }
}
