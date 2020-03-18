package no.nav.tjenestepensjon.simulering.v2.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.JacksonSerializer
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.v2.consumer.model.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Clock
import java.util.*

const val SCOPE = "scope"

@Autowired
lateinit var objectMapper: ObjectMapper

@Component
class MaskinportenTokenConfig {

    @Value("\${IDPORTEN_scope}")
    lateinit var idPortenScope: String

    @Value("\${jwksPublic}")
    lateinit var jwksPublic: String

    @Value("\${clientId}")
    lateinit var clientId: String

    @Value("\${privateKeyBase64}")
    lateinit var privateKeyBase64: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_API_KEY}")
    var maskinportenTokenApiKey: String = "apikey"

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_TOKEN}")
    lateinit var maskinportenTokenEndpoint: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_CONFIGURATION}")
    lateinit var idPortenConfigurationApiGwEndpoint: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_CONFIGURATION_API_KEY}")
    lateinit var maskinportenConfigurationApiKey: String

    private val webClient = WebClientConfig.webClient()

    fun generateToken(): String {
        LOG.info(jwksPublic)
        val jwsToken = generatePrivateJWT()
        try {
            return getTokenFromDIFI(jwsToken)
        } catch (e: Exception) {
            LOG.error("An Error occured wile trying to generate token: $e")
            // TODO exception handling?
            throw e
        }
    }

    fun getKeys(keys: String) = objectMapper.readValue<Keys>(keys)

    internal fun base64ToPrivateKey(privateBase64: String): PrivateKey? {
        LOG.info("From base64 key to PrivateKey")
        val keyBytes: ByteArray = Base64.getDecoder().decode(privateBase64)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val fact: KeyFactory = KeyFactory.getInstance("RSA")
        return fact.generatePrivate(keySpec)
    }

    fun generatePrivateJWT(): Jws {
        LOG.info("Getting Apps own private key and generating JWT token")
        LOG.info("Generating JWS token")

        val keys = getKeys(jwksPublic).keys
        return Jws(
                Jwts.builder()
                        .setHeaderParams(
                                mapOf(
                                        JwsHeader.KEY_ID to keys.map { it.kid }.single(),
                                        JwsHeader.ALGORITHM to keys.map { it.alg }.single()
                                )
                        )
                        .setAudience(getMaskinportenAuthrozationServerConfiguration())
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


    fun getMaskinportenAuthrozationServerConfiguration(): String {
        LOG.info("Getting own certificate and generating keypair and certificate")
        return try {
            LOG.info("Getting well-known configuration from id-porten at: ${idPortenConfigurationApiGwEndpoint}")
            webClient.get()
                    .uri(idPortenConfigurationApiGwEndpoint)
                    .header("x-nav-apiKey", maskinportenConfigurationApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<MaskinportenConfiguration>() {})
                    .block()
        } catch (e: Exception) {
            LOG.error("Error getting config from idporten: ${idPortenConfigurationApiGwEndpoint}", e)
            throw IllegalStateException(e)
        }.let {
            LOG.info("Got config for idporten")
            it.issuer
        }
    }

    fun getTokenFromDIFI(jwsToken: Jws): String {
        LOG.info("Making a Formdata request Url-encoded: to - ${maskinportenTokenEndpoint}")

        val token = jwsToken.token
        val body = BodyInserters.fromObject("""grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$token""")

        return try {
            webClient.post()
                    .uri(maskinportenTokenEndpoint)
                    .body(body)
                    .header("x-nav-apiKey", maskinportenTokenApiKey)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<IdPortenAccessTokenResponse>() {})
                    .block()
        } catch (e: Throwable) {
            LOG.error("IdPorten: Unexpected error while fetching access token",e)
            throw e
        }.let { AccessToken(it.accessToken).token }
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}