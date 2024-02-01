package no.nav.tjenestepensjon.simulering.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import no.nav.tjenestepensjon.simulering.service.AADClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig {

    @Bean
    fun httpClient(): HttpClient =
        HttpClient.create().proxyWithSystemProperties().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .doOnConnected { it.addHandlerLast(ReadTimeoutHandler(READ_TIMEOUT_MILLIS / 1000)) }

    @Bean
    fun webClient(httpClient: HttpClient): WebClient =
        WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient)).build()

    @Bean
    fun afpBeholdningWebClient(
        @Value("\${afp.beholdning.url}") baseUrl: String,
        @Value("\${afp.beholdning.scope}") scope: String,
        httpClient: HttpClient,
        adClient: AADClient
    ): WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .filter { request, next ->
            next.exchange(
                ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, adClient.getToken(scope)) //TODO cache token
                    .build()
            )
        }
        .build()

    @Bean
    fun penWebClient(
        @Value("\${pen.url}") baseUrl: String,
        @Value("\${pen.scope}") scope: String,
        httpClient: HttpClient,
        adClient: AADClient
    ): WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .filter { request, next ->
            next.exchange(
                ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, adClient.getToken(scope)) //TODO cache token
                    .build()
            )
        }
        .build()

    companion object {
        private const val CONNECT_TIMEOUT_MILLIS = 3000
        const val READ_TIMEOUT_MILLIS = 5000
    }
}
