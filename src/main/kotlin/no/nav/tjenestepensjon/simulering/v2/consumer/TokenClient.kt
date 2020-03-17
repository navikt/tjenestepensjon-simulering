package no.nav.tjenestepensjon.simulering.v2.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.JacksonSerializer
import no.nav.tjenestepensjon.simulering.v2.consumer.model.Jws
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Clock
import java.util.*

const val SCOPE = "scope"

@Autowired
lateinit var objectMapper: ObjectMapper

class TokenClient {

    var idPortenScope: String = "klp:pensjonsimulering"


    @Value("\${jwksPublic}")
    lateinit var jwksPublic: String

    @Value("\${clientId}")
    lateinit var clientId: String

    @Value("\${privateKeyBase64}")
    lateinit var privateKeyBase64: String

    fun generateToken() {
        //log.info { jwksPublic }
        val jwsToken = generatePrivateJWT()
        try {
            getTokenFromDIFI(jwsToken)
        } catch (e: Exception) {
            // log.error { "An Error occured wile trying to generate token: $e" }
            // exception handling?
        }
    }

    fun getKeys(keys: String) = objectMapper.readValue<Keys>(keys)

    data class Keys(
            val keys: List<Jwks>
    )

    data class Jwks(
            val kty: String,
            val e: String,
            val use: String,
            val kid: String,
            val alg: String,
            val n: String
    )

    internal fun base64ToPrivateKey(privateBase64: String): PrivateKey? {
//    log.info { "From base64 key to PrivateKey" }
        val keyBytes: ByteArray = Base64.getDecoder().decode(privateBase64)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val fact: KeyFactory = KeyFactory.getInstance("RSA")
        return fact.generatePrivate(keySpec)
    }


    fun generatePrivateJWT(): Jws {

//    log.info { "Getting Apps own private key and generating JWT token" }
//    log.info { "Generating JWS token" }
        val keys = getKeys(jwksPublic).keys
        return Jws(
                Jwts.builder()
                        .setHeaderParams(
                                mapOf(
                                        JwsHeader.KEY_ID to keys.map { it.kid }.single(),
                                        JwsHeader.ALGORITHM to keys.map { it.alg }.single()
                                )
                        )
                        .setAudience("httsp://maskinporten.no/")//getOIDCWellKnownConfiguration())
                        .setIssuer(clientId)
                        .setIssuedAt(Date(Clock.systemUTC().millis()))
                        .setId(UUID.randomUUID().toString())
                        .setExpiration(Date(Clock.systemUTC().millis() + 120000))
                        .claim(SCOPE, idPortenScope)
                        .serializeToJsonWith(JacksonSerializer<Map<String, Any?>>(objectMapper))
                        .signWith(base64ToPrivateKey(privateKeyBase64) as PrivateKey, SignatureAlgorithm.RS256)
                        .compact()
        )
    }
}