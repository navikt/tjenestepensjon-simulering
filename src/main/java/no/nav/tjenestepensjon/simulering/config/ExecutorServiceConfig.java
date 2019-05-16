package no.nav.tjenestepensjon.simulering.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorServiceConfig {

    @Bean
    public ExecutorService taskExecutor() {
        //TODO Inject number of providers? Should have 1 thread per provider.
        return Executors.newFixedThreadPool(7);
    }
}
