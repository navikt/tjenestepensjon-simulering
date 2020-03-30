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
import no.nav.tjenestepensjon.simulering.v2.exceptions.MaskinportenException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.ProxyProvider
import reactor.netty.tcp.TcpClient
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Clock
import java.util.*
import java.net.ProxySelector

@Service
class MaskinportenTokenProvider {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Value("\${IDPORTEN_scope}")
    lateinit var idPortenScope: String

    @Value("\${jwk_public}")
    lateinit var jwksPublic: String

    @Value("\${client_id}")
    lateinit var clientId: String

    @Value("\${private_key_base64}")
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
                        .claim(SCOPE, idPortenScope)
                        .serializeToJsonWith(JacksonSerializer<Map<String, Any?>>(objectMapper))
                        .signWith(base64ToPrivateKey(privateKeyBase64) as PrivateKey, SignatureAlgorithm.RS256)
                        .compact()
        )
    }


    fun getMaskinportenAuthrozationServerConfiguration(): String {
        LOG.info("Getting own certificate and generating keypair and certificate")
        return try {

            LOG.info("Setting proxy for httpClient: ${ProxySelector.getDefault().toString()}")

            val httpClient = HttpClient.create()
                    .tcpConfiguration { tcpClient -> tcpClient.proxy { proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(ProxySelector.getDefault().toString()) } }
            val connector = ReactorClientHttpConnector(httpClient)
            val client = WebClient.builder().clientConnector(connector).build()

            LOG.info("Getting well-known configuration from id-porten at: ${idPortenConfigurationApiGwEndpoint}")
            client.get()
                    .uri(idPortenConfigurationApiGwEndpoint)
                    .header("x-nav-apiKey", maskinportenConfigurationApiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<MaskinportenConfiguration>() {})
                    .block()
        } catch (e: Exception) {
            LOG.error("idPortenConfigurationApiGwEndpoint: $idPortenConfigurationApiGwEndpoint, maskinportenConfigurationApiKey: $maskinportenConfigurationApiKey")
            LOG.error("Error getting config from idporten: ${idPortenConfigurationApiGwEndpoint}", e)
            throw ConnectToIdPortenException(e.message)
        }.let {
            LOG.info("Got config for idporten")
            it.issuer
        }
    }

    @Throws(MaskinportenException::class)
    fun generateToken(): String {
        LOG.info(jwksPublic)
        val jwsToken = try {
            generatePrivateJWT().token
        } catch (e: Throwable) {
            LOG.error("An Error occured wile trying to generate token: $e")
            throw e
        }

        LOG.info("Making a Formdata request Url-encoded: to - $maskinportenTokenEndpoint")
        return try {
            webClient.post()
                    .uri(maskinportenTokenEndpoint)
                    .bodyValue("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwsToken")
                    .header("x-nav-apiKey", maskinportenTokenApiKey)
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<IdPortenAccessTokenResponse>() {})
                    .block()
        } catch (e: Throwable) {
            LOG.error("Maskinporten: Unexpected error while fetching access token",e)
            throw ConnectToMaskinPortenException(e.message)
        }.let { AccessToken(it.accessToken).token }
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)

        const val SCOPE = "scope"
    }
}