package no.nav.tjenestepensjon.simulering.rest;

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.consumer.TokenClient;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class RestClient implements Tjenestepensjonsimulering {
    private WebClient webClient = WebClient.create();

    private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);

    private final TokenClient tokenClient;

    public RestClient(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning) {
        List<Stillingsprosent> stillingsprosenter = webClient.get()
                .uri("http://localhost")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + tokenClient.getOidcAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Stillingsprosent>>() {})
                .block();
        return stillingsprosenter;
    }

    @Override
    public List<OutgoingResponse.SimulertPensjon> simulerPensjon(IncomingRequest incomingRequest, List<TPOrdning> tpOrdningList, TPOrdning latest) {
        List<OutgoingResponse.SimulertPensjon> simuletPensjonListe = webClient.get()
                .uri("http://localhost")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + tokenClient.getOidcAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<OutgoingResponse.SimulertPensjon>>() {})
                .block();
        return simuletPensjonListe;
    }
}
