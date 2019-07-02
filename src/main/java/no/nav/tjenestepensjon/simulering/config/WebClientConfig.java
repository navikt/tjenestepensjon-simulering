package no.nav.tjenestepensjon.simulering.config;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

public class WebClientConfig {

    public static final Integer CONNECT_TIMEOUT_MILLIS = 3000;
    public static final Integer READ_TIMEOUT_MILLIS = 5000;

    public static WebClient webClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient())))
                .build();
    }

    private static TcpClient tcpClient() {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MILLIS / 1000));
                });
    }
}
