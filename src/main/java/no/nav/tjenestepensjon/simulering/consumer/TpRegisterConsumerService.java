package no.nav.tjenestepensjon.simulering.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TpRegisterConsumerService implements TpRegisterConsumer {

    private String tpRegisterUrl;
    private WebClient webClient = WebClient.create();

    @Autowired
    private TokenClient tokenClient;

    @Value("${TP_REGISTERET_URL}")
    public void setTpRegisterUrl(String tpRegisterUrl) {
        this.tpRegisterUrl = tpRegisterUrl;
    }

    @Override
    public List<TPOrdning> getTpOrdningerForPerson(String fnr) throws NoTpOrdningerFoundException {
        List<TPOrdning> tpOrdningerFromTpRegisteret = webClient.get()
                .uri(tpRegisterUrl + "/person/" + fnr + "/tpordninger")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + tokenClient.getOidcAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TPOrdning>>() {})
                .block();

        if(tpOrdningerFromTpRegisteret.size() == 0) {
            throw new NoTpOrdningerFoundException("No Tp-ordning found for person:" + fnr);
        }

        return tpOrdningerFromTpRegisteret;
    }
}
