package no.nav.tjenestepensjon.simulering.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.ProxyProvider.Proxy.HTTP
import reactor.netty.tcp.TcpClient

@Configuration
class WebClientConfig {

    private final val tcpClient: TcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .doOnConnected { it.addHandlerLast(ReadTimeoutHandler(READ_TIMEOUT_MILLIS / 1000)) }
            .proxy { it.type(HTTP).host("webproxy-nais.nav.no").port(8088).nonProxyHosts(".*(tpregisteret|tpconfig|security-token-service).*") }

    @get:Bean
    val webClient: WebClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .build()

    companion object{
        private const val CONNECT_TIMEOUT_MILLIS = 3000
        const val READ_TIMEOUT_MILLIS = 5000
    }
}