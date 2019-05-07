package no.nav.tjenestepensjon.simulering.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ExecutorService taskExecutor() {
        //TODO select reasonable number
        return Executors.newFixedThreadPool(4);
    }
}
