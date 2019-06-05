package no.nav.tjenestepensjon.simulering.consumer;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

@Service
public class TpRegisterConsumerService implements TpRegisterConsumer {

    private String tpRegisterUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private TokenClient tokenClient;

    @Value("${TP_REGISTERET_URL}")
    public void setTpRegisterUrl(String tpRegisterUrl) {
        this.tpRegisterUrl = tpRegisterUrl;
    }

    @Override
    public List<TPOrdning> getTpOrdningerForPerson(String fnr) throws NoTpOrdningerFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "Bearer " + tokenClient.getOidcAccessToken());
        ResponseEntity<List<TPOrdning>> responseEntity = restTemplate
                .exchange(tpRegisterUrl + "/person/" + fnr + "/tpordninger",
                        HttpMethod.GET,
                        new HttpEntity<>("parameters", headers),
                        new ParameterizedTypeReference<List<TPOrdning>>() {
                        });
        if (responseEntity.getBody().size() == 0) {
            throw new NoTpOrdningerFoundException("No Tp-ordning found for person:" + fnr);
        }
        return responseEntity.getBody();
    }
}
