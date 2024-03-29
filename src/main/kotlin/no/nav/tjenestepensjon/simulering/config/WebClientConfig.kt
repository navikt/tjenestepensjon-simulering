package no.nav.tjenestepensjon.simulering.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import no.nav.tjenestepensjon.simulering.config.CorrelationIdFilter.Companion.CONSUMER_ID
import no.nav.tjenestepensjon.simulering.config.CorrelationIdFilter.Companion.CONSUMER_ID_HTTP_HEADER
import no.nav.tjenestepensjon.simulering.config.CorrelationIdFilter.Companion.CORRELATION_ID
import no.nav.tjenestepensjon.simulering.config.CorrelationIdFilter.Companion.CORRELATION_ID_HTTP_HEADER
import no.nav.tjenestepensjon.simulering.service.AADClient
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun httpClient(): HttpClient =
        HttpClient.create().proxyWithSystemProperties().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .doOnConnected { it.addHandlerLast(ReadTimeoutHandler(READ_TIMEOUT_MILLIS / 1000)) }

    @Bean
    fun webClient(httpClient: HttpClient): WebClient =
        WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient))
            .filter { request, next -> addCorrelationId(next, request) }
            .build()

    @Bean
    fun afpBeholdningWebClient(
        @Value("\${afp.beholdning.url}") baseUrl: String,
        @Value("\${afp.beholdning.scope}") afpScope: String,
        builder: WebClient.Builder,
        httpClient: HttpClient,
        adClient: AADClient
    ): WebClient = builder
        .baseUrl(baseUrl)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .filter { request, next ->
            next.exchange(
                ClientRequest.from(request)
                    .headers { it.setBearerAuth(adClient.getToken(afpScope)) }
                    .build()
            )
        }
        .filter { request, next -> addCorrelationId(next, request) }
        .build()

    @Bean
    fun penWebClient(
        @Value("\${pen.url}") baseUrl: String,
        @Value("\${pen.scope}") scope: String,
        builder: WebClient.Builder,
        httpClient: HttpClient,
        adClient: AADClient,
    ): WebClient = builder
        .baseUrl(baseUrl)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .filter { request, next ->
            next.exchange(
                ClientRequest.from(request)
                    .headers { it.setBearerAuth(adClient.getToken(scope)) }
                    .build()
            )
        }
        .filter { request, next -> addCorrelationId(next, request) }
        .build()

    private fun addCorrelationId(
        next: ExchangeFunction,
        request: ClientRequest
    ): Mono<ClientResponse> = next.exchange(
        ClientRequest.from(request)
            .header(CORRELATION_ID_HTTP_HEADER, MDC.get(CORRELATION_ID))
            .header(CONSUMER_ID_HTTP_HEADER, MDC.get(CONSUMER_ID))
            .build()
    )

    companion object {
        private const val CONNECT_TIMEOUT_MILLIS = 3000
        const val READ_TIMEOUT_MILLIS = 5000
    }
}
