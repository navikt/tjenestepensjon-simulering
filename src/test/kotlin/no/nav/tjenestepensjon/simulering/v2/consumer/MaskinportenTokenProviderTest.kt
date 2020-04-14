package no.nav.tjenestepensjon.simulering.v2.consumer

import com.github.tomakehurst.wiremock.WireMockServer
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class, ObjectMapperConfig::class])
@AutoConfigureMockMvc
internal class MaskinportenTokenProviderTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var tokenclient: MaskinportenTokenProvider

    @Test
    fun `Test marshalling of HentStillingsprosentListeRequest`() {
//        tokenclient.generateToken()

//        val map: Map<String, List<String>> = mapOf("grant_type" to listOf("urn:ietf:params:oauth:grant-type:jwt-bearer"), "assertion" to listOf("jwsToken.token"))
//
//        System.out.println("Parameters ${map()}")
    }

    companion object {
        private var wireMockServer = WireMockServer()
                .apply { start() }
                .also(TokenProviderStub::configureTokenProviderStub)

        @JvmStatic
        @AfterAll
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}