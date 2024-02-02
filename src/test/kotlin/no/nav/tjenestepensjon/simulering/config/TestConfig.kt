package no.nav.tjenestepensjon.simulering.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class TestConfig {
    @Bean
    fun webClientbuilder() = WebClient.builder()
}