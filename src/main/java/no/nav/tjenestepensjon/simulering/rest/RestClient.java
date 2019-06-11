package no.nav.tjenestepensjon.simulering.rest;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.tjenestepensjon.simulering.Tjenestepensjonsimulering;
import no.nav.tjenestepensjon.simulering.consumer.TokenClient;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

@Component
public class RestClient implements Tjenestepensjonsimulering {
    private WebClient webClient = WebClient.create();

    private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);

    private final TokenClient tokenClient;

    public RestClient(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor) {
        return webClient.get()
                .uri(tpLeverandor.getUrl())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + tokenClient.getOidcAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Stillingsprosent>>() {
                })
                .block();
    }

    @Override
    public List<SimulertPensjon> simulerPensjon(IncomingRequest request, TPOrdning tpOrdning, TpLeverandor tpLeverandor,
            Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap) {
        return webClient.get()
                .uri(tpLeverandor.getUrl())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + tokenClient.getOidcAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SimulertPensjon>>() {
                })
                .block();
    }
}
