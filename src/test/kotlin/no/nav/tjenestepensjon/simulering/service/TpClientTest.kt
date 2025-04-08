package no.nav.tjenestepensjon.simulering.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.config.TestConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.defaultFNRString
import no.nav.tjenestepensjon.simulering.defaultForhold
import no.nav.tjenestepensjon.simulering.defaultTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.defaultTpid
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.consumer.MaskinportenTokenClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpregisteretException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.assertTrue

@SpringBootTest(classes = [TpClient::class, WebClientConfig::class, ObjectMapperConfig::class, TestConfig::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TpClientTest {

    @MockitoBean
    private lateinit var tokenClient: AADClient

    @Autowired
    private lateinit var tpClient: TpClient

    @MockitoBean
    private lateinit var maskinportenTokenClient: MaskinportenTokenClient

    private val aktiveOrdningerRequest: MappingBuilder =
        get(urlPathEqualTo(AKTIVE_ORDNINGER_URL))
            .withHeader("fnr", equalTo(defaultFNRString))

    private val server = WireMockServer().apply { start() }

    @BeforeAll
    fun beforeAll() {
        `when`(tokenClient.getToken(anyNonNull())).thenReturn("bogustoken")
    }

    @AfterAll
    fun afterAll() {
        server.stop()
    }

    @Test
    fun `findForhold med ett forhold`() {
        val stub = server.stubFor(defaultTjenestepensjonRequest.willReturn(okJson(defaultForhold)))

        val response = tpClient.findForhold(defaultFNRString)

        assertEquals(defaultTpid, response.first().ordning)
        server.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold uten forhold`() {
        val stub = server.stubFor(
            defaultTjenestepensjonRequest.willReturn(okJson("""{"_embedded":{"forholdModelList":[]}}"""))
        )

        assertTrue(tpClient.findForhold(defaultFNRString).isEmpty())
        server.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold person ikke funnet`() {
        val stub = server.stubFor(defaultTjenestepensjonRequest.willReturn(notFound()))

        assertTrue(tpClient.findForhold(defaultFNRString).isEmpty())

        server.removeStub(stub.uuid)
    }

    @Test
    fun `findForhold unauthorized`() {
        val stub = server.stubFor(defaultTjenestepensjonRequest.willReturn(unauthorized()))

        assertThrows<TpregisteretException> { tpClient.findForhold(defaultFNRString) }

        server.removeStub(stub.uuid)
    }

    @Test
    fun `findTPForhold med ett forhold`() {
        val stub = server.stubFor(aktiveOrdningerRequest.willReturn(okJson(ORDNING)))

        val response = tpClient.findTPForhold(fnr = defaultFNRString)

        with(response.first()) {
            assertEquals("foo", navn)
            assertEquals("1234", tpNr)
            assertEquals("2345", orgNr)
            assertEquals("a1", alias[0])
        }
        server.removeStub(stub.uuid)
    }

    private companion object {
        private const val AKTIVE_ORDNINGER_URL = "/api/tjenestepensjon/aktiveOrdninger"

        private const val ORDNING = """[
    {
        "navn": "foo",
        "tpNr": "1234",
        "orgNr": "2345",
        "alias": [
            "a1"
        ]
    }
]"""
    }
}
