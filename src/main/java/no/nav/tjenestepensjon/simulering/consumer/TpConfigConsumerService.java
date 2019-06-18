package no.nav.tjenestepensjon.simulering.consumer;

import static no.nav.tjenestepensjon.simulering.config.CacheConfig.TP_ORDNING_LEVERANDOR_CACHE;
import static no.nav.tjenestepensjon.simulering.config.WebClientConfig.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Service
public class TpConfigConsumerService implements TpConfigConsumer {

    private String tpConfigUrl;
    private WebClient webClient = webClient();

    @Value("${TP_CONFIG_URL}")
    public void setTpConfigUrl(String tpConfigUrl) {
        this.tpConfigUrl = tpConfigUrl;
    }

    @Override
    @Cacheable(value = TP_ORDNING_LEVERANDOR_CACHE)
    public String findTpLeverandor(TPOrdning tpOrdning) {
        return webClient.get()
                .uri(tpConfigUrl + "/tpleverandoer/" + tpOrdning.getTpId())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
