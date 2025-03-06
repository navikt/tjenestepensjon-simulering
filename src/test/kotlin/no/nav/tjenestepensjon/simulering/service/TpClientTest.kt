package no.nav.tjenestepensjon.simulering.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.*
import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.config.TestConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.consumer.FssGatewayAuthService
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpregisteretException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.assertTrue

@SpringBootTest(classes = [TpClient::class, WebClientConfig::class, ObjectMapperConfig::class, TestConfig::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TpClientTest {

    @MockitoBean
    private lateinit var aadClient: AADClient

    @MockitoBean
    private lateinit var fssGatewayAuthService: FssGatewayAuthService

    @Autowired
    private lateinit var tpClient: TpClient

    @MockitoBean
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

        val response = tpClient.findForhold(defaultFNRString)

        assertEquals(defaultTpid, response.first().ordning)

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold uten forhold`() {
        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(okJson("""{"_embedded":{"forholdModelList":[]}}"""))
        )

        assertTrue(tpClient.findForhold(defaultFNRString).isEmpty())

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold person ikke funnet`() {
        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(notFound())
        )

        assertTrue(tpClient.findForhold(defaultFNRString).isEmpty())

        wireMockServer.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold unauthorized`() {
        val stub = wireMockServer.stubFor(
            defaultTjenestepensjonRequest.willReturn(unauthorized())
        )

        assertThrows<TpregisteretException> { tpClient.findForhold(defaultFNRString) }

        wireMockServer.removeStub(stub.uuid)

    }

}