package no.nav.tjenestepensjon.simulering.soap;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.HentStillingsprosentListeResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.mapper.StillingsprosentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SoapClient extends WebServiceGatewaySupport {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    public List<Stillingsprosent> hentStillingsprosentListe() {
        var request = new ObjectFactory().createHentStillingsprosentListeRequest();
        var response = (HentStillingsprosentListeResponse) webServiceTemplate.marshalSendAndReceive(request);
        var mapper = new StillingsprosentMapper();

        return response.getStillingsprosentListe().stream()
            .map(mapper::mapToStillingsprosent)
            .collect(Collectors.toList());
    }
}