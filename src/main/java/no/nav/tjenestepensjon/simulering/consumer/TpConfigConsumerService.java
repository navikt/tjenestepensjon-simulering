package no.nav.tjenestepensjon.simulering.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TpConfigConsumerService implements TpConfigConsumer {

    private String tpConfigUrl;
    private WebClient webClient = WebClient.create();

    @Value("${TP_CONFIG_URL}")
    public void setTpConfigUrl(String tpConfigUrl) {
        this.tpConfigUrl = tpConfigUrl;
    }

    @Override
    public String findTpLeverandor(TPOrdning tpOrdning) {
        //TODO should probably cache
        return webClient.get()
                .uri(tpConfigUrl + "/tpleverandoer/" + tpOrdning.getTpId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
