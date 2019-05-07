package no.nav.tjenestepensjon.simulering.soap;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.List;

@Component
public class SoapClient {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    public List<Stillingsprosent> hentStillingsprosentListe() {
        return null;
    }
}