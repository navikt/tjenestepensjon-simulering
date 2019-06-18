package no.nav.tjenestepensjon.simulering.rest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static no.nav.tjenestepensjon.simulering.config.WebClientConfig.webClient;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
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
    private WebClient webClient = webClient();

    private final TokenClient tokenClient;

    public RestClient(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Override
    public List<Stillingsprosent> getStillingsprosenter(String fnr, TPOrdning tpOrdning, TpLeverandor tpLeverandor) {
        return webClient.get()
                .uri(tpLeverandor.getUrl())
                .header(AUTHORIZATION, "Bearer " + tokenClient.getOidcAccessToken())
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
                .header(AUTHORIZATION, "Bearer " + tokenClient.getOidcAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SimulertPensjon>>() {
                })
                .block();
    }
}
