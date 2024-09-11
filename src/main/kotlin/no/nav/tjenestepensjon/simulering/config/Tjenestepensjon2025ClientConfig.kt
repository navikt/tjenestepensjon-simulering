package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.WebClientConfig.Companion.addCorrelationId
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPTjenestepensjonClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKTjenestepensjonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import kotlin.reflect.KFunction0

@Configuration
class Tjenestepensjon2025ClientConfig {

    @Bean("oftp-2025-klp")
    fun klpTjenestepensjonV2025Client(
        @Value("\${oftp.2025.klp.endpoint.url}") url: String,
        webClientBuilder: WebClient.Builder,
        tokenClient: MaskinportenTokenClient,
    ): TjenestepensjonV2025Client {
        return KLPTjenestepensjonClient(webClientBuilder
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient
                        .create()
                        .proxyWithSystemProperties()
                        .followRedirect(true)
                )
            )
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .baseUrl(url)
            .filter { request, next -> addCorrelationId(next, request) }
            .filter { request, next -> authorize(request, next, tokenClient::pensjonsimuleringToken) }
            .build()
        )
    }

    //TODO konfigurer SPK client - implementeres i PEK-504
    @Bean("oftp-2025-spk")
    fun spkTjenestepensjonV2025Client(): TjenestepensjonV2025Client {
        return SPKTjenestepensjonClient()
    }

    private fun authorize(request: ClientRequest, next: ExchangeFunction, tokenFunction: KFunction0<String>): Mono<ClientResponse> =
        next.exchange(
            ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenFunction.invoke()}")
                .build()
        )
}