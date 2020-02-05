/*
 * Copyright 2012-2019 the original author or authors.
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
 */
package no.nav.tjenestepensjon.simulering.config.jwk

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import org.springframework.core.convert.converter.Converter
import org.springframework.util.StringUtils
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * A [Converter] that converts the supplied `InputStream` to a `Set` of [JwkDefinition](s).
 * The source of the `InputStream` **must be** a JWK Set representation which is a JSON object
 * that has a &quot;keys&quot; member and its value is an array of JWKs.
 * <br></br>
 * <br></br>
 * **NOTE:** The Key Type (&quot;kty&quot;) currently supported by this [Converter] is [JwkDefinition.KeyType.RSA].
 * <br></br>
 * <br></br>
 *
 * @author Joe Grandja
 * @author Vedran Pavic
 * @author Michael Duergner
 * @see JwkDefinition
 *
 * @see [JWK Set Format](https://tools.ietf.org/html/rfc7517.page-10)
 */
internal class JwkSetConverter : Converter<InputStream?, Set<JwkDefinition>> {
    private val factory = JsonFactory()
    /**
     * Converts the supplied `InputStream` to a `Set` of [JwkDefinition](s).
     *
     * @param jwkSetSource the source for the JWK Set
     * @return a `Set` of [JwkDefinition](s)
     * @throws JwkException if the JWK Set JSON object is invalid
     */
    override fun convert(jwkSetSource: InputStream?): Set<JwkDefinition> {
        val jwkDefinitions: MutableSet<JwkDefinition>
        var parser: JsonParser? = null
        try {
            parser = factory.createParser(jwkSetSource)
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw JwkException("Invalid JWK Set Object.")
            }
            if (parser.nextToken() != JsonToken.FIELD_NAME) {
                throw JwkException("Invalid JWK Set Object.")
            }
            if (parser.currentName != JwkAttributes.KEYS) {
                throw JwkException("Invalid JWK Set Object. The JWK Set MUST have a " + JwkAttributes.KEYS + " attribute.")
            }
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw JwkException("Invalid JWK Set Object. The JWK Set MUST have an array of JWK(s).")
            }
            jwkDefinitions = LinkedHashSet()
            val attributes: MutableMap<String, String> = HashMap()
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                attributes.clear()
                while (parser.nextToken() == JsonToken.FIELD_NAME) {
                    val attributeName = parser.currentName
                    // gh-1082 - skip arrays such as x5c as we can't deal with them yet
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                        }
                    } else {
                        attributes[attributeName] = parser.valueAsString
                    }
                }
                // gh-1470 - skip unsupported public key use (enc) without discarding the entire set
                val publicKeyUse: JwkDefinition.PublicKeyUse = JwkDefinition.PublicKeyUse.Companion.fromValue(attributes[JwkAttributes.PUBLIC_KEY_USE])
                if (JwkDefinition.PublicKeyUse.ENC == publicKeyUse) {
                    continue
                }
                var jwkDefinition: JwkDefinition? = null
                val keyType: JwkDefinition.KeyType = JwkDefinition.KeyType.Companion.fromValue(attributes[JwkAttributes.KEY_TYPE])
                if (JwkDefinition.KeyType.RSA == keyType) {
                    jwkDefinition = createRsaJwkDefinition(attributes)
                } else if (JwkDefinition.KeyType.EC == keyType) {
                    jwkDefinition = createEllipticCurveJwkDefinition(attributes)
                }
                if (jwkDefinition?.let(jwkDefinitions::add) == false)
                    throw JwkException("Duplicate JWK found in Set: ${jwkDefinition.keyId} (${JwkAttributes.KEY_ID})")
            }
        } catch (ex: IOException) {
            throw JwkException("An I/O error occurred while reading the JWK Set: " + ex.message, ex)
        } finally {
            try {
                parser?.close()
            } catch (_: IOException) {}
        }
        return jwkDefinitions
    }

    /**
     * Creates a [RsaJwkDefinition] based on the supplied attributes.
     *
     * @param attributes the attributes used to create the [RsaJwkDefinition]
     * @return a [JwkDefinition] representation of a RSA Key
     * @throws JwkException if at least one attribute value is missing or invalid for a RSA Key
     */
    private fun createRsaJwkDefinition(attributes: Map<String, String>): JwkDefinition { // kid
        val keyId = attributes[JwkAttributes.KEY_ID]
        if (!StringUtils.hasText(keyId)) {
            throw JwkException(JwkAttributes.KEY_ID + " is a required attribute for a JWK.")
        }
        // use
        val publicKeyUse: JwkDefinition.PublicKeyUse = JwkDefinition.PublicKeyUse.Companion.fromValue(attributes[JwkAttributes.PUBLIC_KEY_USE])
        if (JwkDefinition.PublicKeyUse.SIG != publicKeyUse)
            throw JwkException("${publicKeyUse.value()} (${JwkAttributes.PUBLIC_KEY_USE}) is currently not supported.")
        // alg
        val algorithm: JwkDefinition.CryptoAlgorithm = JwkDefinition.CryptoAlgorithm.Companion.fromHeaderParamValue(attributes[JwkAttributes.ALGORITHM])
        if (JwkDefinition.CryptoAlgorithm.RS256 != algorithm
                && JwkDefinition.CryptoAlgorithm.RS384 != algorithm
                && JwkDefinition.CryptoAlgorithm.RS512 != algorithm)
            throw JwkException("${algorithm.standardName()} (${JwkAttributes.ALGORITHM}) is currently not supported.")
        // n
        val modulus = attributes[JwkAttributes.RSA_PUBLIC_KEY_MODULUS]
        if (!StringUtils.hasText(modulus))
            throw JwkException("${JwkAttributes.RSA_PUBLIC_KEY_MODULUS} is a required attribute for a RSA JWK.")
        // e
        val exponent = attributes[JwkAttributes.RSA_PUBLIC_KEY_EXPONENT]
        if (!StringUtils.hasText(exponent))
            throw JwkException("${JwkAttributes.RSA_PUBLIC_KEY_EXPONENT} is a required attribute for a RSA JWK.")
        return RsaJwkDefinition(
                keyId, publicKeyUse, algorithm, modulus, exponent)
    }

    /**
     * Creates an [EllipticCurveJwkDefinition] based on the supplied attributes.
     *
     * @param attributes the attributes used to create the [EllipticCurveJwkDefinition]
     * @return a [JwkDefinition] representation of an EC Key
     * @throws JwkException if at least one attribute value is missing or invalid for an EC Key
     */
    private fun createEllipticCurveJwkDefinition(attributes: Map<String, String>): JwkDefinition { // kid
        val keyId = attributes[JwkAttributes.KEY_ID]
        if (keyId.isNullOrEmpty()) throw JwkException("${JwkAttributes.KEY_ID} is a required attribute for an EC JWK.")
        // use
        val publicKeyUse: JwkDefinition.PublicKeyUse = JwkDefinition.PublicKeyUse.Companion.fromValue(attributes[JwkAttributes.PUBLIC_KEY_USE])
        if (JwkDefinition.PublicKeyUse.SIG != publicKeyUse) {
            throw JwkException("${publicKeyUse.value()} (${JwkAttributes.PUBLIC_KEY_USE}) is currently not supported.")
        }
        // alg
        val algorithm: JwkDefinition.CryptoAlgorithm = JwkDefinition.CryptoAlgorithm.Companion.fromHeaderParamValue(attributes[JwkAttributes.ALGORITHM])
        if (JwkDefinition.CryptoAlgorithm.ES256 != algorithm
                && JwkDefinition.CryptoAlgorithm.ES384 != algorithm
                && JwkDefinition.CryptoAlgorithm.ES512 != algorithm)
            throw JwkException("${algorithm.standardName()} (${JwkAttributes.ALGORITHM}) is currently not supported.")
        // x
        val x = attributes[JwkAttributes.EC_PUBLIC_KEY_X]
        if (x.isNullOrEmpty())
            throw JwkException("${JwkAttributes.EC_PUBLIC_KEY_X} is a required attribute for an EC JWK.")
        // y
        val y = attributes[JwkAttributes.EC_PUBLIC_KEY_Y]
        if (y.isNullOrEmpty())
            throw JwkException("${JwkAttributes.EC_PUBLIC_KEY_Y} is a required attribute for an EC JWK.")
        // crv
        val curve = attributes[JwkAttributes.EC_PUBLIC_KEY_CURVE]
        if (curve.isNullOrEmpty())
            throw JwkException(JwkAttributes.EC_PUBLIC_KEY_CURVE + " is a required attribute for an EC JWK.")
        return EllipticCurveJwkDefinition(
                keyId, publicKeyUse, algorithm, x, y, curve)
    }
}