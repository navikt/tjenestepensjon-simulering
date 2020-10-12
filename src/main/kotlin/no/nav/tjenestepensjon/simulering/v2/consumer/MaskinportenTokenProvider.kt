package no.nav.tjenestepensjon.simulering.v2.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.JacksonSerializer
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.v2.consumer.model.*
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToIdPortenException
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Clock
import java.util.*

@Service
class MaskinportenTokenProvider {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Value("\${PENSJONSIMULERING_SCOPE}")
    lateinit var pensjonsimuleringScope: String

    @Value("\${TPREGISTERET_SCOPE}")
    lateinit var tpregisteretScope: String

    @Value("\${jwk_public}")
    lateinit var jwksPublic: String

    @Value("\${client_id}")
    lateinit var clientId: String

    @Value("\${private_key_base64}")
    lateinit var privateKeyBase64: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_API_KEY}")
    lateinit var maskinportenTokenApiKey: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_TOKEN}")
    lateinit var maskinportenTokenEndpoint: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_CONFIGURATION}")
    lateinit var maskinportenConfigurationApiGwEndpoint: String

    @Value("\${DIFI_ENDPOINTS_MASKINPORTEN_CONFIGURATION_API_KEY}")
    lateinit var maskinportenConfigurationApiKey: String

    @Value("\${PEPROXY_URL}")
    lateinit var peproxyUrl: String

    private val webClient = WebClientConfig.webClient()

    fun getKeys(keys: String) = objectMapper.readValue<Keys>(keys)

    fun generatePensjonsimuleringToken() = generateToken(pensjonsimuleringScope)
    fun generateTpregisteretToken() = generateToken(tpregisteretScope)

    fun generateToken(scope: String): String {
        LOG.info(jwksPublic)
        val jwsToken = try {
            generatePrivateJWT(scope).token
        } catch (e: Throwable) {
            LOG.error("An Error occured wile trying to generate token: $e")
            throw e
        }

        LOG.info("Making a Formdata request Url-encoded: to - $maskinportenTokenEndpoint")
        return try {
            webClient.post()
                    .uri(peproxyUrl)
                    .header(header_target_url, maskinportenTokenEndpoint)
                    .header(header_x_nav_apiKey, maskinportenTokenApiKey)
                    .header(header_content_type, "application/x-www-form-urlencoded")
                    .bodyValue("""grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwsToken""")
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<IdPortenAccessTokenResponse>() {})
                    .block()
        } catch (e: Throwable) {
            LOG.error("Maskinporten: Unexpected error while fetching access token",e)
            throw ConnectToMaskinPortenException(e.message)
        }.let { AccessToken(it.accessToken).token }
    }

    fun generatePrivateJWT(scope: String): Jws {
        LOG.info("Getting Apps own private key and generating JWT token")
        LOG.info("Generating JWS token")

        val key = getKeys(jwksPublic).keys.single()
        return Jws(
                Jwts.builder()
                        .setHeaderParam(JwsHeader.KEY_ID, key.kid)
                        .setHeaderParam(JwsHeader.ALGORITHM, key.alg)
                        .setAudience(getMaskinportenAuthrozationServerConfiguration())
                        .setIssuer(clientId)
                        .setIssuedAt(Date(Clock.systemUTC().millis()))
                        .setId(UUID.randomUUID().toString())
                        .setExpiration(Date(Clock.systemUTC().millis() + 120000))
                        .claim(SCOPE, scope)
                        .serializeToJsonWith(JacksonSerializer<Map<String, Any?>>(objectMapper))
                        .signWith(base64ToPrivateKey() as PrivateKey, SignatureAlgorithm.RS256)
                        .compact()
        )
    }

    fun getMaskinportenAuthrozationServerConfiguration(): String {
        LOG.info("Getting own certificate and generating keypair and certificate")
        return try {
            LOG.info("Getting well-known configuration from maskinporten at: $maskinportenConfigurationApiGwEndpoint")

            webClient.get()
                    .uri(peproxyUrl)
                    .header(header_target_url, maskinportenConfigurationApiGwEndpoint)
                    .header(header_x_nav_apiKey, maskinportenConfigurationApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<MaskinportenConfiguration>() {})
                    .block()
        } catch (e: Exception) {
            LOG.error("maskinportenConfigurationApiGwEndpoint: $maskinportenConfigurationApiGwEndpoint, maskinportenConfigurationApiKey: $maskinportenConfigurationApiKey")
            LOG.error("Error getting config from maskinporten: $maskinportenConfigurationApiGwEndpoint", e)
            throw ConnectToIdPortenException(e.message)
        }.let {
            LOG.info("Got config for idporten")
            it.issuer
        }
    }

    internal fun base64ToPrivateKey(): PrivateKey? {
        LOG.info("From base64 key to PrivateKey")
        val keyBytes: ByteArray = Base64.getDecoder().decode(privateKeyBase64)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val fact: KeyFactory = KeyFactory.getInstance("RSA")
        return fact.generatePrivate(keySpec)
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)

        const val SCOPE = "scope"
        const val header_target_url = "target"
        const val header_x_nav_apiKey="x-nav-apiKey"
        const val header_content_type="content-type"
    }
}