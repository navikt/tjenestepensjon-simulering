package no.nav.tjenestepensjon.simulering.consumer;

import static no.nav.tjenestepensjon.simulering.config.CacheConfig.TP_ORDNING_PERSON_CACHE;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

@Service
public class TpRegisterConsumerService implements TpRegisterConsumer {

    private String tpRegisterUrl;
    private WebClient webClient = WebClient.create();
    private final TokenClient tokenClient;

    public TpRegisterConsumerService(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Value("${TP_REGISTERET_URL}")
    public void setTpRegisterUrl(String tpRegisterUrl) {
        this.tpRegisterUrl = tpRegisterUrl;
    }

    @Override
    @Cacheable(value = TP_ORDNING_PERSON_CACHE)
    public List<TPOrdning> getTpOrdningerForPerson(String fnr) throws NoTpOrdningerFoundException {
        List<TPOrdning> tpOrdningerFromTpRegisteret = webClient.get()
                .uri(tpRegisterUrl + "/person/" + fnr + "/tpordninger")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + tokenClient.getOidcAccessToken().getAccessToken())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TPOrdning>>() {
                })
                .block();

        if (tpOrdningerFromTpRegisteret.size() == 0) {
            throw new NoTpOrdningerFoundException("No Tp-ordning found for person:" + fnr);
        }

        return tpOrdningerFromTpRegisteret;
    }
}
