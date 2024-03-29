package no.nav.tjenestepensjon.simulering.v2.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.domain.Token
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.service.TokenService
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient.TokenType.OIDC
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient.TokenType.SAML
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.net.URI

@Service
class TokenClient(private val webClient: WebClient) : TokenService {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var maskinportenToken: MaskinportenToken

    @Value("\${SERVICE_USER}")
    lateinit var username: String

    @Value("\${SERVICE_USER_PASSWORD}")
    lateinit var password: String

    @Value("\${sts.url}")
    lateinit var stsUrl: String

    fun pensjonsimuleringToken(): String {
        return try {
            maskinportenToken.getToken()
        } catch (exc: Throwable) {
            log.warn { "Error while retrieving token from provider: ${exc.message}" }
            throw ConnectToMaskinPortenException(exc.message)
        }
    }

    private var oidcToken: Token = TokenImpl("", expiresIn = 0)
        get() = if (field.isExpired) getTokenFromProvider(OIDC).also { field = it }
        else field

    private var samlToken: Token = TokenImpl("", expiresIn = 0)
        get() = if (field.isExpired) getTokenFromProvider(SAML).also { field = it }
        else field

    @get:Synchronized
    override val oidcAccessToken: Token
        get() = oidcToken.also { log.info { "Returning cached and valid oidc-token for user: $username" } }

    @get:Synchronized
    override val samlAccessToken: Token
        get() = samlToken.also { log.info { "Returning cached and valid saml-token for user: $username" } }


    private fun getTokenFromProvider(tokenType: TokenType): Token {
        log.info { "Getting new access-token for user: $username from: ${getUrlForType(tokenType)}" }
        return webClient.get().uri(getUrlForType(tokenType)).headers { it.setBasicAuth(username, password) }.retrieve()
            .onStatus({ httpStatusCode: HttpStatusCode -> httpStatusCode != HttpStatus.OK }) {
                throw RuntimeException(
                    "Error while retrieving token from provider, returned HttpStatus ${it.statusCode().value()}"
                )
            }.run {
                try {
                    bodyToMono<TokenImpl>().block()
                } catch (e: Throwable) {
                    throw if (e is RuntimeException) e else RuntimeException("Retrieved invalid token from provider")
                }
            }
    }

    private fun getUrlForType(tokenType: TokenType) = if (OIDC == tokenType) oidcEndpointUrl else samlEndpointUrl

    private val oidcEndpointUrl: URI
        get() = URI.create("$stsUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid")

    private val samlEndpointUrl: URI
        get() = URI.create("$stsUrl/rest/v1/sts/samltoken")

    internal enum class TokenType {
        OIDC, SAML
    }
}
