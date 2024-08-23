package no.nav.tjenestepensjon.simulering.v2.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.domain.Token
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
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
class TokenClient(private val webClient: WebClient) {
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
        get() = if (field.isExpired) getTokenFromProvider().also { field = it }
        else field

    @get:Synchronized
    val oidcAccessToken: Token
        get() = oidcToken.also { log.info { "Returning cached and valid oidc-token for user: $username" } }

    private fun getTokenFromProvider(): Token {
        log.info { "Getting new access-token for user: $username from: $oidcEndpointUrl" }
        return webClient.get().uri(oidcEndpointUrl).headers { it.setBasicAuth(username, password) }.retrieve()
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

    private val oidcEndpointUrl: URI
        get() = URI.create("$stsUrl/rest/v1/sts/token?grant_type=client_credentials&scope=openid")
}
