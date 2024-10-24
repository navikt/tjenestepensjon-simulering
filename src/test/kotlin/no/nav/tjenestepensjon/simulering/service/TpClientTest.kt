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
import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
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

    @MockBean
    private lateinit var fssGatewayAuthService: FssGatewayAuthService

    @Autowired
    private lateinit var tpClient: TpClient

    @MockBean
    private lateinit var maskinportenTokenClient: MaskinportenTokenClient

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

        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(okJson(defaultForhold))
        )

        val response = tpClient.findForhold(defaultFNR)

        assertEquals(defaultTpid, response.first().ordning)

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold uten forhold`() {
        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(okJson("""{"_embedded":{"forholdModelList":[]}}"""))
        )

        assertThrows<NoTpOrdningerFoundException> { tpClient.findForhold(defaultFNR) }

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold person ikke funnet`() {
        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(notFound())
        )

        assertThrows<NoTpOrdningerFoundException> { tpClient.findForhold(defaultFNR) }

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold unauthorized`() {
        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(unauthorized())
        )

        assertThrows<ResponseStatusException> { tpClient.findForhold(defaultFNR) }

        wireMockServer.removeStub(stub.uuid)

    }

}