package no.nav.tjenestepensjon.simulering.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static no.nav.tjenestepensjon.simulering.config.WebClientConfig.READ_TIMEOUT_MILLIS;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.netty.handler.timeout.ReadTimeoutException;

import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer;
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumerService;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

@SpringBootTest(classes = TpConfigConsumerService.class)
class WebClientConfigTest {

    private static WireMockServer wireMockServer;
    @Autowired
    private TpConfigConsumer tpConfigConsumer;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/tp"))
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(READ_TIMEOUT_MILLIS * 2)));
    }

    @Test
    void shouldThrowExceptionifReadTimeoutExceeded() {
        assertThrows(ReadTimeoutException.class, () -> tpConfigConsumer.findTpLeverandor(new TPOrdning("tss", "tp")));
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }
}