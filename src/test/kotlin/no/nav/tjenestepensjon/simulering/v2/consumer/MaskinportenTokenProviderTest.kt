package no.nav.tjenestepensjon.simulering.v2.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.WireMockServer
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.config.TokenProviderStub
import no.nav.tjenestepensjon.simulering.v2.consumer.model.Keys
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
    fun `Test marshalling of HentStillingsprosentListeRequest`(){
        tokenclient.generateToken()
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