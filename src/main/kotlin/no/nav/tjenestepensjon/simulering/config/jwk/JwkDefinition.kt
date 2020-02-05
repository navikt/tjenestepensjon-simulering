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
 */
package no.nav.tjenestepensjon.simulering.config.jwk

abstract class JwkDefinition protected constructor(
        val keyId: String,
        val keyType: KeyType,
        val publicKeyUse: PublicKeyUse,
        val algorithm: CryptoAlgorithm?) {

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null || this.javaClass != obj.javaClass) {
            return false
        }
        val that = obj as JwkDefinition
        return if (keyId != that.keyId) {
            false
        } else keyType == that.keyType
    }

    override fun hashCode(): Int {
        var result = keyId.hashCode()
        result = 31 * result + keyType.hashCode()
        return result
    }

    /**
     * The defined Key Type (&quot;kty&quot;) values.
     */
    internal enum class KeyType(private val value: String) {
        RSA("RSA"), EC("EC"), OCT("oct");

        fun value(): String {
            return value
        }

        companion object {
            fun fromValue(value: String?): KeyType? {
                var result: KeyType? = null
                for (keyType in values()) {
                    if (keyType.value() == value) {
                        result = keyType
                        break
                    }
                }
                return result
            }
        }

    }

    /**
     * The defined Public Key Use (&quot;use&quot;) values.
     */
    internal enum class PublicKeyUse(private val value: String) {
        SIG("sig"), ENC("enc");

        fun value(): String {
            return value
        }

        companion object {
            fun fromValue(value: String?): PublicKeyUse? {
                var result: PublicKeyUse? = null
                for (publicKeyUse in values()) {
                    if (publicKeyUse.value() == value) {
                        result = publicKeyUse
                        break
                    }
                }
                return result
            }
        }

    }

    /**
     * The defined Algorithm (&quot;alg&quot;) values.
     */
    internal enum class CryptoAlgorithm(// JCA Standard Name
            private val standardName: String, private val headerParamValue: String) {
        RS256("SHA256withRSA", "RS256"), RS384("SHA384withRSA", "RS384"), RS512("SHA512withRSA", "RS512"), ES256("SHA256withECDSA", "ES256"), ES384("SHA384withECDSA", "ES384"), ES512("SHA512withECDSA", "ES512");

        fun standardName(): String {
            return standardName
        }

        fun headerParamValue(): String {
            return headerParamValue
        }

        companion object {
            fun fromHeaderParamValue(headerParamValue: String?): CryptoAlgorithm? {
                var result: CryptoAlgorithm? = null
                for (algorithm in values()) {
                    if (algorithm.headerParamValue() == headerParamValue) {
                        result = algorithm
                        break
                    }
                }
                return result
            }
        }

    }

}