package no.nav.tjenestepensjon.simulering.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.ProxyProvider.Proxy.HTTP
import java.net.InetSocketAddress
import java.net.Proxy

@Configuration
@Profile("!noProxy")
class WebClientConfig {

    private val proxyAddress = InetSocketAddress("http://webproxy-nais.nav.no", 8088)

    @Bean
    fun httpClient(): HttpClient =
        HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .doOnConnected { it.addHandlerLast(ReadTimeoutHandler(READ_TIMEOUT_MILLIS / 1000)) }
            .proxy { it.type(HTTP).address(proxyAddress).nonProxyHosts(".*(tp-api|security-token-service|isso).*") }

    @Bean
    fun webClient(httpClient: HttpClient): WebClient =
        WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient)).build()

    @Bean
    fun proxy() = Proxy(Proxy.Type.HTTP, proxyAddress)

    companion object {
        private const val CONNECT_TIMEOUT_MILLIS = 3000
        const val READ_TIMEOUT_MILLIS = 5000
    }
}
