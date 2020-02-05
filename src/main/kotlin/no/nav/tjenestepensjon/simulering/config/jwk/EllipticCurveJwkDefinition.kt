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

internal class EllipticCurveJwkDefinition(keyId: String,
    publicKeyUse: PublicKeyUse,
    algorithm: CryptoAlgorithm,
    val x: String,
    val y: String,
    val curve: String
) : JwkDefinition(keyId, KeyType.EC, publicKeyUse, algorithm) {

    internal enum class NamedCurve(val value: String) {
        P256("P-256"),
        P384("P-384"),
        P521("P-521");
    }
}