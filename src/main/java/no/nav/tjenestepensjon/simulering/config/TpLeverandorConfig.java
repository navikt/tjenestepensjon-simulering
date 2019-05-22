package no.nav.tjenestepensjon.simulering.config;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.valueOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

@Configuration
public class TpLeverandorConfig {

    private String leverandorUrlMap;

    @Value("${TP_LEVERANDOR_URL_MAP}")
    public void setLeverandorUrlMap(String leverandorUrlMap) {
        this.leverandorUrlMap = leverandorUrlMap;
    }

    @Bean
    public List<TpLeverandor> tpLeverandorList() {
        return createListFromEnv(leverandorUrlMap);
    }

    /**
     * Parse env variable to generate a list of TpLeverandor.
     * "," delimits the details of induvidual providers
     * "|" delimits different providers
     *
     * @param leverandorUrlMap env variable format "LEVERANDOR,URL,IMPL|..."
     * @return List of TpLeverandor
     */
    private List<TpLeverandor> createListFromEnv(@NotNull String leverandorUrlMap) {
        List<TpLeverandor> providers = new ArrayList<>();
        Arrays.stream(leverandorUrlMap.split("\\|")).forEach(provider -> providers.add(parseProvider(provider)));
        return providers;
    }

    private TpLeverandor parseProvider(@NotNull String provider) {
        String[] details = provider.split(",");
        assert details.length == 3;
        return new TpLeverandor(details[0], details[1], valueOf(details[2]));
    }
}
