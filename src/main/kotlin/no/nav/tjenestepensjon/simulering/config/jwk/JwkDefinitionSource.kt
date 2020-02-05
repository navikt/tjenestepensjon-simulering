/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications copyright 2019 NAV.
 */
package no.nav.tjenestepensjon.simulering.config.jwk

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import no.nav.tjenestepensjon.simulering.config.jwk.JwkDefinition.KeyType.*
import org.springframework.security.jwt.codec.Codecs
import org.springframework.security.jwt.crypto.sign.EllipticCurveVerifier
import org.springframework.security.jwt.crypto.sign.RsaVerifier
import org.springframework.security.jwt.crypto.sign.SignatureVerifier
import java.io.IOException
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.Proxy.Type.HTTP
import java.net.URL
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.concurrent.ConcurrentHashMap

class JwkDefinitionSource(private val tokenProviders: List<TokenProvider>) {
    private val jwkDefinitions: MutableMap<String, JwkDefinitionHolder> = ConcurrentHashMap()

    fun getDefinitionLoadIfNecessary(keyId: String): JwkDefinitionHolder? = jwkDefinitions[keyId]
            ?: synchronized(jwkDefinitions) {
                jwkDefinitions[keyId] ?: {
                            jwkDefinitions.clear()
                            tokenProviders.forEach { jwkDefinitions.putAll(loadJwkDefinitions(it)) }
                            jwkDefinitions[keyId]
                        }()
            }

    class JwkDefinitionHolder(val jwkDefinition: JwkDefinition, val signatureVerifier: SignatureVerifier)

    companion object {
        private val jwkSetConverter = JwkSetConverter()

        fun loadJwkDefinitions(tokenProvider: TokenProvider) = try {
            URL(tokenProvider.jwksUrl).let {
                if (tokenProvider.proxyUrl != null) {
                    val proxyUrl = tokenProvider.proxyUrl.split(':')
                    it.openConnection(Proxy(HTTP, InetSocketAddress(proxyUrl[0], Integer.valueOf(proxyUrl[1])))).getInputStream()
                } else {
                    it.openStream()
                }
            }.let(jwkSetConverter::convert)
                    .associateBy(JwkDefinition::keyId)
                    .mapNotNull { (key, jwkDefinition) ->
                        when (jwkDefinition.keyType) {
                            RSA -> key to JwkDefinitionHolder(jwkDefinition, createRsaVerifier(jwkDefinition as RsaJwkDefinition))
                            EC -> key to JwkDefinitionHolder(jwkDefinition, createEcVerifier(jwkDefinition as EllipticCurveJwkDefinition))
                            OCT -> null
                        }
                    }.toMap()
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("Invalid JWK Set URL: ${e.message}", e)
        } catch (e: IOException) {
            throw JwkException("An I/O error occurred while reading from the JWK Set source: ${e.message}", e)
        }

        private fun createRsaVerifier(rsaDefinition: RsaJwkDefinition) = try {
            (KeyFactory.getInstance("RSA")
                    .generatePublic(RSAPublicKeySpec(
                            BigInteger(1, Codecs.b64UrlDecode(rsaDefinition.modulus)),
                            BigInteger(1, Codecs.b64UrlDecode(rsaDefinition.exponent)))
                    ) as RSAPublicKey
            ).let { rsaPublicKey ->
                rsaDefinition.algorithm
                        ?.let { RsaVerifier(rsaPublicKey, it.standardName()) }
                        ?: RsaVerifier(rsaPublicKey)
            }
        } catch (ex: Exception) {
            throw JwkException("An error occurred while creating a RSA Public Key Verifier for ${rsaDefinition.keyId} : ${ex.message}", ex)
        }

        private fun createEcVerifier(ecDefinition: EllipticCurveJwkDefinition) = try {
            EllipticCurveVerifier(
                    BigInteger(1, Codecs.b64UrlDecode(ecDefinition.x)),
                    BigInteger(1, Codecs.b64UrlDecode(ecDefinition.y)),
                    ecDefinition.curve,
                    when (ecDefinition.curve) {
                        EllipticCurveJwkDefinition.NamedCurve.P256.value -> JwkDefinition.CryptoAlgorithm.ES256
                        EllipticCurveJwkDefinition.NamedCurve.P384.value -> JwkDefinition.CryptoAlgorithm.ES384
                        EllipticCurveJwkDefinition.NamedCurve.P521.value -> JwkDefinition.CryptoAlgorithm.ES512
                        else -> null
                    }?.standardName()
            )
        } catch (ex: Exception) {
            throw JwkException("An error occurred while creating an EC Public Key Verifier for ${ecDefinition.keyId} : ${ex.message}", ex)
        }
    }
}