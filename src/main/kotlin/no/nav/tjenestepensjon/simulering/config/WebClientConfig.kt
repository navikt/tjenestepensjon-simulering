package no.nav.tjenestepensjon.simulering.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient

object WebClientConfig {
    private const val CONNECT_TIMEOUT_MILLIS = 3000
    const val READ_TIMEOUT_MILLIS = 5000
    fun webClient() = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient())))
            .build()

    private fun tcpClient() = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
            .doOnConnected { connection: Connection -> connection.addHandlerLast(ReadTimeoutHandler(READ_TIMEOUT_MILLIS / 1000)) }
}