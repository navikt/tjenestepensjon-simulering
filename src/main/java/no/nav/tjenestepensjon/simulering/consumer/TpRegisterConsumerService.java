package no.nav.tjenestepensjon.simulering.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Service
public class TpRegisterConsumerService implements TpRegisterConsumer {

    @Value("${TP_REGISTERET_URL}")
    private String tpRegisterUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<TPOrdning> getTpOrdningerForPerson(String fnr) {
        ResponseEntity<List<TPOrdning>> responseEntity = restTemplate
                .exchange(tpRegisterUrl + "/person/" + fnr + "/tpordninger",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TPOrdning>>() {
                        });
        return responseEntity.getBody();
    }
}
