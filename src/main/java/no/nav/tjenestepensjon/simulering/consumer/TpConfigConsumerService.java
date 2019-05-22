package no.nav.tjenestepensjon.simulering.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Service
public class TpConfigConsumerService implements TpConfigConsumer {

    private String tpConfigUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${TP_CONFIG_URL}")
    public void setTpConfigUrl(String tpConfigUrl) {
        this.tpConfigUrl = tpConfigUrl;
    }

    @Override
    public String findTpLeverandor(TPOrdning tpOrdning) {
        //TODO should probably cache
        return restTemplate.getForEntity(tpConfigUrl + "/tpleverandoer/" + tpOrdning.getTpId(), String.class).getBody();
    }
}
