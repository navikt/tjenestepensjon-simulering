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
 * Shared attribute values used by [JwkTokenStore] and associated collaborators.
 *
 * @author Joe Grandja
 * @author Michael Duergner
 */
internal object JwkAttributes {
    const val KEY_ID = "kid"
    const val KEY_TYPE = "kty"
    const val ALGORITHM = "alg"
    const val PUBLIC_KEY_USE = "use"
    const val RSA_PUBLIC_KEY_MODULUS = "n"
    const val RSA_PUBLIC_KEY_EXPONENT = "e"
    const val EC_PUBLIC_KEY_X = "x"
    const val EC_PUBLIC_KEY_Y = "y"
    const val EC_PUBLIC_KEY_CURVE = "crv"
    const val KEYS = "keys"
}