package no.nav.tjenestepensjon.simulering.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ExecutorServiceConfig {
    @Bean
    fun taskExecutor(tpLeverandorConfig: TpLeverandorConfig): ExecutorService =
            Executors.newFixedThreadPool(tpLeverandorConfig.tpLeverandorList().size)
}