package no.nav.tjenestepensjon.simulering.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class ExecutorServiceConfig {
    @Bean
    fun taskExecutor(tpLeverandorConfigOld: TpLeverandorConfigOld): ExecutorService =
            Executors.newFixedThreadPool(tpLeverandorConfigOld.tpLeverandorListOld().size)
}