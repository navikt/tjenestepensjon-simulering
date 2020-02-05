package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.netty.handler.timeout.ReadTimeoutException
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.config.WebClientConfig.READ_TIMEOUT_MILLIS
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumerService
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TpConfigConsumerService::class])
internal class WebClientConfigTest {
    @Autowired
    private val tpConfigConsumer: TpConfigConsumer? = null

    @Test
    fun shouldThrowExceptionifReadTimeoutExceeded() {
        Assertions.assertThrows(ReadTimeoutException::class.java) { tpConfigConsumer.findTpLeverandor(TPOrdning("tss", "tp")) }
    }

    companion object {
        private var wireMockServer: WireMockServer? = null
        @BeforeAll
        fun beforeAll() {
            wireMockServer = WireMockServer()
            wireMockServer!!.start()
            wireMockServer!!.stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/tp"))
                    .willReturn(WireMock.aResponse()
                            .withFixedDelay(READ_TIMEOUT_MILLIS * 2)))
        }

        @AfterAll
        fun afterAll() {
            wireMockServer!!.stop()
        }
    }
}