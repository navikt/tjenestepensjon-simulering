package no.nav.tjenestepensjon.simulering.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.config.WebClientConfig.Companion.READ_TIMEOUT_MILLIS
import no.nav.tjenestepensjon.simulering.defaultLeveradorUrl
import no.nav.tjenestepensjon.simulering.defaultTpid
import no.nav.tjenestepensjon.simulering.defaultTssid
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.service.TpService
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(classes = [TpService::class, WebClientConfig::class])
@TestInstance(PER_CLASS)
internal class WebClientConfigTest {

    @MockBean
    private lateinit var tokenClient: TokenClient

    @Autowired
    private lateinit var tpService: TpService

    private var wireMockServer = WireMockServer().apply {
        start()
        setGlobalFixedDelay(READ_TIMEOUT_MILLIS * 2)
        stubFor(get(urlPathEqualTo(defaultLeveradorUrl)).willReturn(aResponse()))
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `Should throw exception if read timeout exceeded`() {
        assertThrows<RuntimeException> { tpService.findTpLeverandor(TPOrdning(defaultTssid, defaultTpid)) }
    }
}
