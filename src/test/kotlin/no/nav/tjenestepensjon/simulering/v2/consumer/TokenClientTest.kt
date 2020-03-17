package no.nav.tjenestepensjon.simulering.v2.consumer

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [TokenClient::class])
internal class TokenClientTest() {

    @Autowired
    lateinit var tokenclient: TokenClient

    @Test
    fun `Test marshalling of HentStillingsprosentListeRequest`(){

//        tokenclient.generateToken()

    }
}