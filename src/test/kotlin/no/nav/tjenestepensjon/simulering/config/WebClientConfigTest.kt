package no.nav.tjenestepensjon.simulering.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.netty.handler.timeout.ReadTimeoutException
import no.nav.tjenestepensjon.simulering.config.WebClientConfig.Companion.READ_TIMEOUT_MILLIS
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumerService
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TpConfigConsumerService::class, WebClientConfig::class])
internal class WebClientConfigTest {

    @Autowired
    private lateinit var tpConfigConsumer: TpConfigConsumer

    @Test
    fun `Should throw exception if read timeout exceeded`() {
        assertThrows<ReadTimeoutException>{ tpConfigConsumer.findTpLeverandor(TPOrdning("tss", "tp")) }
    }

    companion object {
        private var wireMockServer = WireMockServer().apply {
            start()
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/tpleverandoer/tp"))
                    .willReturn(WireMock.aResponse()
                            .withFixedDelay(READ_TIMEOUT_MILLIS * 2)))
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}