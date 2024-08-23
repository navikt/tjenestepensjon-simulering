package no.nav.tjenestepensjon.simulering.v1.consumer

import no.nav.tjenestepensjon.simulering.domain.Token
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.service.TokenService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Profile(value = ["prod-gcp", "dev-gcp"])
@Service
class GatewayTokenClient(private val soapGatewayAuthWebClient: WebClient) : TokenService {

    override val oidcAccessToken: Token?
        get() = null //not supported
    override var samlAccessToken: Token = TokenImpl("", expiresIn = -1)
        get() = if (field.isExpired) hentSamlToken().also { field = it }
        else field

    private fun hentSamlToken(): Token {
        return soapGatewayAuthWebClient
            .post()
            .uri("/rest/v1/sts/token/exchange")
            .retrieve()
            .bodyToMono(TokenImpl::class.java)
            .block() ?: throw RuntimeException("Failed to fetch SAML token from fss-gateway")
    }
}