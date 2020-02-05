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

/**
 * A JSON Web Key (JWK) representation of a RSA key.
 *
 * @see [JSON Web Key
 * @see [JSON Web Algorithms
 * @author Joe Grandja
](https://tools.ietf.org/html/rfc7518.page-30)](https://tools.ietf.org/html/rfc7517) */
internal class RsaJwkDefinition
/**
 * Creates an instance of a RSA JSON Web Key (JWK).
 *
 * @param keyId the Key ID
 * @param publicKeyUse the intended use of the Public Key
 * @param algorithm the algorithm intended to be used
 * @param modulus the modulus value for the Public Key
 * @param exponent the exponent value for the Public Key
 */(keyId: String?,
    publicKeyUse: PublicKeyUse,
    algorithm: CryptoAlgorithm?,
    val modulus: String?,
    val exponent: String?) : JwkDefinition(keyId, KeyType.RSA, publicKeyUse, algorithm) {
    /**
     * @return the modulus value for the Public Key
     */
    /**
     * @return the exponent value for the Public Key
     */

}