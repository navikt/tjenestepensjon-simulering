package no.nav.tjenestepensjon.simulering.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class ProxylessWebClientConfig {
    @Bean
    fun webClient(): WebClient = WebClient.create()
}
