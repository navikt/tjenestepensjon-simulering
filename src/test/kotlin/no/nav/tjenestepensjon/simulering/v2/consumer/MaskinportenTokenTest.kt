package no.nav.tjenestepensjon.simulering.v2.consumer

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MaskinportenTokenConfig::class])
internal class MaskinportenTokenTest() {

    @Autowired
    lateinit var tokenclient: MaskinportenTokenConfig

    @Test
    fun `Test marshalling of HentStillingsprosentListeRequest`(){

//        tokenclient.generateToken()

    }
}