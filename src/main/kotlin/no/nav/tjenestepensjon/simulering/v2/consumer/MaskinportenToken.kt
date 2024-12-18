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
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
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
        .build { k: String -> fetchToken(k) }

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
                .expirationTime(twoMinutesFromNow())
                .build()
        )
        signedJWT.sign(RSASSASigner(rsaKey.toRSAPrivateKey()))
        val response = webClient.post().uri(endpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Accept", "*/*")
            .body(
                BodyInserters
                .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .with("assertion", signedJWT.serialize()))
            .retrieve()
            .bodyToMono(MaskinportenTokenResponse::class.java)
            .block()
        log.info { "Hentet token fra maskinporten med scope(s): ${scope}" }
        return response!!.access_token
    }

    fun twoMinutesFromNow(): Date {
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
        private const val EXPIRE_AFTER: Long = 100
        private const val REQUEST_TOKEN_TO_EXPIRE_AFTER_SECONDS: Int = (EXPIRE_AFTER + 20).toInt()
    }

}