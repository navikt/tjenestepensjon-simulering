package no.nav.tjenestepensjon.simulering.v1.consumer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.domain.Token
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.service.TokenService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException

@Profile(value = ["prod-gcp", "dev-gcp"])
@Service
class GatewayTokenClient(private val soapGatewayAuthWebClient: WebClient,
                         private val fssGatewayAuthService: FssGatewayAuthService,
    ) : TokenService {
    private val log = KotlinLogging.logger {}
    override var oidcAccessToken: Token = TokenImpl("", expiresIn = -1)
        get() = if (field.isExpired) hentOidcToken().also { field = it }
        else field
    override var samlAccessToken: Token = TokenImpl("", expiresIn = -1)
        get() = if (field.isExpired) hentSamlToken().also { field = it }
        else field

    private fun hentSamlToken(): Token {
        return try {
            val token = fssGatewayAuthService.hentToken() ?: throw RuntimeException("Tried to get a token from fssGatewayAuthService from fss")
            soapGatewayAuthWebClient
                .post()
                .uri(TOKEN_EXCHANGE_PATH)
                .headers { it.setBearerAuth(token) }
                .body(body(token))
                .retrieve()
                .bodyToMono(TokenImpl::class.java)
                .block()
                .also { log.info { "Hentet SAML token fra fss-gateway" } } ?: throw RuntimeException("Failed to fetch SAML token from fss-gateway")
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to fetch SAML, WebClientRequestException" }
            throw RuntimeException(DEFAULT_ERROR_MSG, e)
        } catch (e: WebClientResponseException) {
            log.error(e) { "Failed to fetch SAML, WebClientResponseException" }
            throw RuntimeException(DEFAULT_ERROR_MSG, e)
        }
    }

    private fun hentOidcToken(): Token {
        return try {
            val token = fssGatewayAuthService.hentToken() ?: throw RuntimeException("Tried to get a OIDC token from fssGatewayAuthService from fss")
            soapGatewayAuthWebClient
                .get()
                .uri(TOKEN_PATH)
                .headers { it.setBearerAuth(token) }
                .retrieve()
                .bodyToMono(TokenImpl::class.java)
                .block()
                .also { log.info { "Hentet OIDC token fra fss-gateway" } } ?: throw RuntimeException("Failed to fetch OIDC token from fss-gateway")
        } catch (e: WebClientRequestException) {
            log.error(e) { "Failed to fetch OIDC, WebClientRequestException" }
            throw RuntimeException(DEFAULT_ERROR_MSG, e)
        } catch (e: WebClientResponseException) {
            log.error(e) { "Failed to fetch OIDC, WebClientResponseException" }
            throw RuntimeException(DEFAULT_ERROR_MSG, e)
        }
    }

    companion object {
        private const val DEFAULT_ERROR_MSG = "Failed to fetch SAML token from fss-gateway"
        private const val TOKEN_EXCHANGE_PATH = "/rest/v1/sts/token/exchange?serviceUserId=3"
        private const val TOKEN_PATH = "/rest/v1/sts/token?grant_type=client_credentials&scope=openid&serviceUserId=3"

        private fun body(token: String) =
            BodyInserters
                .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                .with("subject_token_type", "urn:ietf:params:oauth:token-type:access_token")
                .with("subject_token", token)
    }
}