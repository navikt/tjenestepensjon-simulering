package no.nav.tjenestepensjon.simulering.v2.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.JacksonSerializer
import junit.framework.Assert.assertNotNull
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringApplication
import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.v2.consumer.model.Jws
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.security.PrivateKey
import java.time.Clock
import java.util.*

@SpringBootTest(classes = [TjenestepensjonSimuleringApplication::class, ObjectMapperConfig::class])
@AutoConfigureMockMvc
internal class MaskinportenTokenProviderTest {

    @Value("\${jwk_public}")
    lateinit var jwksPublic: String

    @Autowired
    lateinit var maskinportenTokenProvider: MaskinportenTokenProvider

    @Autowired
    lateinit var objectMapper: ObjectMapper

    //@Test
    fun `Checking if private key can be converted to privatekey`() {
        maskinportenTokenProvider.base64ToPrivateKey()
    }

    //@Test
    fun `Creating a jwts grant`() {
        val key = maskinportenTokenProvider.getKeys(jwksPublic).keys.single()

        assertNotNull(Jws(
                Jwts.builder()
                        .setHeaderParam(JwsHeader.KEY_ID, key.kid)
                        .setHeaderParam(JwsHeader.ALGORITHM, key.alg)
                        .setAudience("audiance")
                        .setIssuer("clientId")
                        .setIssuedAt(Date(Clock.systemUTC().millis()))
                        .setId(UUID.randomUUID().toString())
                        .setExpiration(Date(Clock.systemUTC().millis() + 120000))
                        .claim(MaskinportenTokenProvider.SCOPE, "test:scope")
                        .serializeToJsonWith(JacksonSerializer<Map<String, Any?>>(objectMapper))
                        .signWith(maskinportenTokenProvider.base64ToPrivateKey(), SignatureAlgorithm.RS256)
                        .compact()
        ))
    }
}