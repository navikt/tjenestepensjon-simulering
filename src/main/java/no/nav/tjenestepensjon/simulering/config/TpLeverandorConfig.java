package no.nav.tjenestepensjon.simulering.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

@Configuration
public class TpLeverandorConfig {

    @Value("TP_LEVERANDOR_URL_MAP")
    private String leverandorUrlMap;

    @Bean
    public List<TpLeverandor> tpLeverandorList() {
        return List.of(new TpLeverandor("1", "url1"), new TpLeverandor("2", "url2"), new TpLeverandor("3", "url3"));
    }

    /**
     * Parse env variable to generate a list of TpLeverandor.
     *
     * @param leverandorUrlMap env variable format "LEVERANDOR:URL,IMPL"
     * @return List of TpLeverandor
     */
    private List<TpLeverandor> createListFromEnv(String leverandorUrlMap) {
        return null;
    }
}
