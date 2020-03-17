package no.nav.tjenestepensjon.simulering.v2.consumer

import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.v2.consumer.model.AccessToken
import no.nav.tjenestepensjon.simulering.v2.consumer.model.IdPortenAccessTokenResponse
import no.nav.tjenestepensjon.simulering.v2.consumer.model.Jws
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.BodyInserters


private val webClient = WebClientConfig.webClient()

var idPortenTokenApiKey: String = "apikey"

var idPortenTokenApiGw: String = "https://oidc-ver2.difi.no/idporten-oidc-provider/token"


//    fun getOIDCWellKnownConfiguration(): IdPortenOidcConfiguration {
//        //log.info { "Getting own certificate and generating keypair and certificate" }
//        return try {
//            //log.info { "Getting well-known configuration from id-porten at: ${idPortenConfigurationApiGwEndpoint}" }
//
//            webClient.get()
//                    .uri(idPortenConfigurationApiGwEndpoint)
//                    .header("x-nav-apiKey", apiGwApiKey)
//                    .header(HttpHeaders.ACCEPT, ContentType.Application.Json.toString())
//                    .retrieve()
//                    .bodyToMono(object : ParameterizedTypeReference<IdPortenOidcConfiguration>() {})
//                    .block()
//        } catch (e: Exception) {
//            //log.error { "Error getting config from idporten: ${idPortenConfigurationApiGwEndpoint}" }
//            throw IllegalStateException(e).also {
//                System.exit(1)
//            }
//        }.also {
//            //log.info { "Got config for idporten" }
//            it.issuer
//        }
//    }

fun getTokenFromDIFI(jwsToken: Jws): AccessToken {
    //log.info { "Making a Formdata request Url-encoded: to - ${idPortenTokenApiGw}" }

    val token = jwsToken.token
    val body = BodyInserters.fromObject(
            """grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$token"""
    )

    return try {
        webClient.post()
                .uri(idPortenTokenApiGw)
                .body(body)
                .header("x-nav-apiKey", idPortenTokenApiKey)
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<IdPortenAccessTokenResponse>() {})
                .block()
    } catch (e: Exception) {
        //log.error(e) { "IdPorten: Unexpected error while fetching access token" }
        throw e
    }.let { AccessToken(it.accessToken) }
}

