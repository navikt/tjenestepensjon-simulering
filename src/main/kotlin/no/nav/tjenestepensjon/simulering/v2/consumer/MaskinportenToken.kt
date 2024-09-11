package no.nav.tjenestepensjon.simulering.v2.consumer

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

@Service
class MaskinportenToken(
    private val webClient: WebClient,
    @Value("\${maskinporten.client-id}") val clientId: String,
    @Value("\${maskinporten.client-jwk}") val clientJwk: String,
    @Value("\${maskinporten.scope}") val scopes: String,
    @Value("\${maskinporten.issuer}") val issuer: String,
    @Value("\${maskinporten.token-endpoint-url}") val endpoint: String,
) {
    private val log = KotlinLogging.logger {}

    fun getToken(): String {
        val rsaKey = RSAKey.parse(clientJwk)
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.keyID)
                .type(JOSEObjectType.JWT)
                .build(),
            JWTClaimsSet.Builder()
                .audience(issuer)
                .issuer(clientId)
                .claim("scope", scopes)
                .issueTime(Date())
                .expirationTime(twoMinutesFromDate(Date()))
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
        log.info { "Hentet token fra maskinporten ${response}" }
        log.info { "Token fra maskinporten with following scopes: ${scopes}" }
        return response!!.access_token
    }

    fun twoMinutesFromDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date;
        calendar.add(Calendar.MINUTE, 2)

        return calendar.time
    }

    data class MaskinportenTokenResponse(
        val access_token: String,
        val token_type: String,
        val expires_in: Int,
        val scope: String,
    )

}