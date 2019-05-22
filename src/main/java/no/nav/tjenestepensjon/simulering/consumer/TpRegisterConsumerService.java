package no.nav.tjenestepensjon.simulering.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

@Service
public class TpRegisterConsumerService implements TpRegisterConsumer {

    private String tpRegisterUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${TP_REGISTERET_URL}")
    public void setTpRegisterUrl(String tpRegisterUrl) {
        this.tpRegisterUrl = tpRegisterUrl;
    }

    @Override
    public List<TPOrdning> getTpOrdningerForPerson(String fnr) throws NoTpOrdningerFoundException {
        ResponseEntity<List<TPOrdning>> responseEntity = restTemplate
                .exchange(tpRegisterUrl + "/person/" + fnr + "/tpordninger",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TPOrdning>>() {
                        });
        if (responseEntity.getBody().size() == 0) {
            throw new NoTpOrdningerFoundException("No Tp-ordning found for person:" + fnr);
        }
        return responseEntity.getBody();
    }
}
