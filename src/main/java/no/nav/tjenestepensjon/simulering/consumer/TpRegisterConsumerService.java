package no.nav.tjenestepensjon.simulering.consumer;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import static no.nav.tjenestepensjon.simulering.config.CacheConfig.TP_ORDNING_PERSON_CACHE;
import static no.nav.tjenestepensjon.simulering.config.WebClientConfig.webClient;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;

@Service
public class TpRegisterConsumerService implements TpRegisterConsumer {

    private String tpRegisterUrl;
    private WebClient webClient = webClient();
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
                .header(AUTHORIZATION, "Bearer " + tokenClient.getOidcAccessToken().getAccessToken())
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
