package no.nav.tjenestepensjon.simulering.v2.consumer

import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MaskinportenTokenProvider::class, ObjectMapperConfig::class])
internal class MaskinportenTokenProviderTest() {

    @Autowired
    lateinit var tokenclient: MaskinportenTokenProvider

    @Test
    fun `Test marshalling of HentStillingsprosentListeRequest`(){

//        tokenclient.generateToken()

    }
}