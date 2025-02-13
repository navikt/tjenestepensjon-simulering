package no.nav.tjenestepensjon.simulering.v2.consumer

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2.exceptions.ConnectToMaskinPortenException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.retry.Retry
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class MaskinportenToken(
    private val webClient: WebClient,
    @Value("\${maskinporten.client-id}") val clientId: String,
    @Value("\${maskinporten.client-jwk}") val clientJwk: String,
    @Value("\${maskinporten.issuer}") val issuer: String,
    @Value("\${maskinporten.token-endpoint-url}") val endpoint: String,
) {
    private val log = KotlinLogging.logger {}
    private val tokenCache: LoadingCache<String, String> = Caffeine.newBuilder()
        .expireAfterWrite(EXPIRE_AFTER, EXPIRE_AFTER_TIME_UNITS)
        .build { k: String ->
            try {
                fetchToken(k)
            } catch (e: RuntimeException) {
                val exc = e.cause ?: e
                log.error(exc) { "Failed to fetch maskinporten token due to ${e.message}" }
                throw exc
            }
        }

    fun getToken(scope: String): String {
        return tokenCache.get(scope)
    }

    fun fetchToken(scope: String): String {
        val rsaKey = RSAKey.parse(clientJwk)
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.keyID)
                .type(JOSEObjectType.JWT)
                .build(),
            JWTClaimsSet.Builder()
                .audience(issuer)
                .issuer(clientId)
                .claim("scope", scope)
                .issueTime(Date())
                .expirationTime(getExpireAfter())
                .build()
        )
        signedJWT.sign(RSASSASigner(rsaKey.toRSAPrivateKey()))
        val response =
            webClient.post().uri(endpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Accept", "*/*")
                .body(
                    BodyInserters
                        .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                        .with("assertion", signedJWT.serialize())
                )
                .retrieve()
                .bodyToMono(MaskinportenTokenResponse::class.java)
                .retryWhen(
                    Retry.backoff(4, java.time.Duration.ofSeconds(2))
                        .maxBackoff(java.time.Duration.ofSeconds(10))
                        .jitter(0.3) // 30% tilfeldig forsinkelse
                        .doBeforeRetry { retrySignal ->
                            log.warn { "Retrying due to: ${retrySignal.failure().message}, attempt: ${retrySignal.totalRetries() + 1}" }
                        }
                        .onRetryExhaustedThrow { _, r -> ConnectToMaskinPortenException("Failed to connect to maskinporten after ${r.totalRetries()} retries and failure due to ${r.failure().message}") }
                )
                .block()
        log.debug { "Hentet token fra maskinporten med scope(s): ${scope}" }
        return response!!.access_token
    }

    fun getExpireAfter(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date();
        calendar.add(Calendar.SECOND, REQUEST_TOKEN_TO_EXPIRE_AFTER_SECONDS)
        return calendar.time
    }


    data class MaskinportenTokenResponse(
        val access_token: String,
        val token_type: String,
        val expires_in: Int,
        val scope: String,
    )

    companion object {
        private val EXPIRE_AFTER_TIME_UNITS = TimeUnit.SECONDS
        private const val EXPIRE_AFTER: Long = 50
        private const val REQUEST_TOKEN_TO_EXPIRE_AFTER_SECONDS: Int = (EXPIRE_AFTER + 20).toInt()
    }

}