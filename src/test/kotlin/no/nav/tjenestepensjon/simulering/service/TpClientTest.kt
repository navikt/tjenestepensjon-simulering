package no.nav.tjenestepensjon.simulering.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.config.TestConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.defaultFNR
import no.nav.tjenestepensjon.simulering.defaultForhold
import no.nav.tjenestepensjon.simulering.defaultTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.defaultTpid
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.server.ResponseStatusException

@SpringBootTest(classes = [TpClient::class, WebClientConfig::class, ObjectMapperConfig::class, TestConfig::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TpClientTest {

    @MockBean
    private lateinit var aadClient: AADClient

    @Autowired
    private lateinit var tpClient: TpClient

    private var wireMockServer = WireMockServer().apply {
        start()
        //setGlobalFixedDelay(WebClientConfig.READ_TIMEOUT_MILLIS * 2) // To force timeout
    }

    @BeforeAll
    fun beforeAll() {
        Mockito.`when`(aadClient.getToken(anyNonNull())).thenReturn("bogustoken")
    }

    @AfterAll
    fun afterAll() {
        wireMockServer.stop()
    }

    @Test
    fun `findForhold med ett forhold`() {

        wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(okJson(defaultForhold))
        )

        val response = tpClient.findForhold(defaultFNR)

        assertEquals(defaultTpid, response.first().ordning)
    }

    @Test
    fun `findForhold uten forhold`() {
        wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(okJson("""{"_embedded":{"forholdDtoList":[]}}"""))
        )

        assertThrows<NoTpOrdningerFoundException> { tpClient.findForhold(defaultFNR) }
    }

    @Test
    fun `findForhold person ikke funnet`() {
        wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(notFound())
        )

        assertThrows<NoTpOrdningerFoundException> { tpClient.findForhold(defaultFNR) }
    }

    @Test
    fun `findForhold unauthorized`() {
        wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(unauthorized())
        )

        assertThrows<ResponseStatusException> { tpClient.findForhold(defaultFNR) }

    }

}