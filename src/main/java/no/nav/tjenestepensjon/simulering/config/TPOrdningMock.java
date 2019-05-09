package no.nav.tjenestepensjon.simulering.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@Configuration
public class TPOrdningMock {

    @Bean
    public List<TPOrdning> tpOrdningList() {
        return List.of(new TPOrdning("1", "1"), new TPOrdning("2", "2"), new TPOrdning("3", "3"));
    }
}
