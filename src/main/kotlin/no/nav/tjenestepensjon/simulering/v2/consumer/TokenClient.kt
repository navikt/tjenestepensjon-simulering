package no.nav.tjenestepensjon.simulering.v2.consumer

import no.nav.tjenestepensjon.simulering.consumer.TokenServiceConsumer
import no.nav.tjenestepensjon.simulering.domain.Token
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient.TokenType.OIDC
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient.TokenType.SAML
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

@Service
class TokenClient(val webClient: WebClient) : TokenServiceConsumer {

    @Autowired
    lateinit var maskinportenTokenProvider: MaskinportenTokenProvider

    @Value("\${SERVICE_USER}")
    lateinit var username: String

    @Value("\${SERVICE_USER_PASSWORD}")
    lateinit var password: String

    @Value("\${STS_URL}")
    lateinit var stsUrl: String

    val pensjonsimuleringToken: String
        get() = maskinportenTokenProvider.generatePensjonsimuleringToken()
    val tpregisteretToken: String
        get() = maskinportenTokenProvider.generateTpregisteretToken()

    private var oidcToken: Token = TokenImpl("", expiresIn = 0)
        get() =
            if (field.isExpired) getTokenFromProvider(OIDC).also { field = it }
            else field

    private var samlToken: Token = TokenImpl("", expiresIn = 0)
        get() =
            if (field.isExpired) getTokenFromProvider(SAML).also { field = it }
            else field

    @get:Synchronized
    override val oidcAccessToken: Token
        get() = oidcToken
                .also { LOG.info("Returning cached and valid oidc-token for user: $username") }

    @get:Synchronized
    override val samlAccessToken: Token
        get() = samlToken
                .also { LOG.info("Returning cached and valid saml-token for user: $username") }


    private fun getTokenFromProvider(tokenType: TokenType): Token {
        LOG.info("Getting new access-token for user: $username from: ${getUrlForType(tokenType)}")
        return webClient.get()
                .uri(getUrlForType(tokenType))
                .headers { it.setBasicAuth(username, password) }
                .retrieve()
                .onStatus({ httpStatus: HttpStatus -> httpStatus != HttpStatus.OK }) { throw RuntimeException("Error while retrieving token from provider, returned HttpStatus:" + it.statusCode().value()) }
                .run {
                    try {
                        bodyToMono<TokenImpl>().block()
                    } catch (_: Throwable) {
                        throw RuntimeException("Retrieved invalid token from provider")
                    }
                }
    }

    private fun getUrlForType(tokenType: TokenType) =
            if (OIDC == tokenType) oidcEndpointUrl else samlEndpointUrl

    private val oidcEndpointUrl: URI
        get() = URI.create("$stsUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid")

    private val samlEndpointUrl: URI
        get() = URI.create("$stsUrl/rest/v1/sts/samltoken")

    internal enum class TokenType {
        OIDC, SAML
    }

    companion object {
        @JvmStatic
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val LOG = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}