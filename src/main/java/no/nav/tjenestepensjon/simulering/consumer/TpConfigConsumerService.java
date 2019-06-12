package no.nav.tjenestepensjon.simulering.consumer;

import static no.nav.tjenestepensjon.simulering.config.CacheConfig.TP_ORDNING_LEVERANDOR_CACHE;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Service
public class TpConfigConsumerService implements TpConfigConsumer {

    private String tpConfigUrl;
    private WebClient webClient = WebClient.create();

    @Value("${TP_CONFIG_URL}")
    public void setTpConfigUrl(String tpConfigUrl) {
        this.tpConfigUrl = tpConfigUrl;
    }

    @Override
    @Cacheable(value = TP_ORDNING_LEVERANDOR_CACHE)
    public String findTpLeverandor(TPOrdning tpOrdning) {
        return webClient.get()
                .uri(tpConfigUrl + "/tpleverandoer/" + tpOrdning.getTpId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
