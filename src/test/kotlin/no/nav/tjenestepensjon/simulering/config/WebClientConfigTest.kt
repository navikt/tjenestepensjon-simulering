package no.nav.tjenestepensjon.simulering.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.config.WebClientConfig.Companion.READ_TIMEOUT_MILLIS
import no.nav.tjenestepensjon.simulering.defaultLeveradorUrl
import no.nav.tjenestepensjon.simulering.defaultTpid
import no.nav.tjenestepensjon.simulering.defaultTssid
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.service.TpClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.AfterTest

@SpringBootTest
internal class WebClientConfigTest {

    @Autowired
    private lateinit var tpClient: TpClient

    private var wireMockServer = WireMockServer().apply {
        start()
        setGlobalFixedDelay(READ_TIMEOUT_MILLIS * 2)
        stubFor(get(urlPathEqualTo(defaultLeveradorUrl)).willReturn(aResponse()))
    }

    @AfterTest
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `Should throw exception if read timeout exceeded`() {
        assertThrows<RuntimeException> {
            val findTpLeverandorName = tpClient.findTpLeverandorName(TPOrdningIdDto(defaultTssid, defaultTpid))
            print(findTpLeverandorName)
        }
    }
}
