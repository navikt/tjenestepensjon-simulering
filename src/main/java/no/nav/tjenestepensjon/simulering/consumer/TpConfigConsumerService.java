package no.nav.tjenestepensjon.simulering.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Service
public class TpConfigConsumerService implements TpConfigConsumer {

    private final String tpConfigUrl = "http://tpconfig";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String findTpLeverandor(TPOrdning tpOrdning) {
        //TODO should probably cache
        return restTemplate.getForEntity(tpConfigUrl + "/tpleverandoer/" + tpOrdning.getTpId(), String.class).getBody();
    }
}
