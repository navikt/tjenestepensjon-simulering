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

import no.nav.tjenestepensjon.simulering.config.jwk.JwkAttributes.ALGORITHM
import no.nav.tjenestepensjon.simulering.config.jwk.JwkAttributes.KEY_ID
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.common.util.JsonParserFactory
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AccessTokenConverter.EXP
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter

class JwkVerifyingJwtAccessTokenConverter(
        private val jwkDefinitionSource: JwkDefinitionSource
) : JwtAccessTokenConverter() {
    private val jwtHeaderConverter = JwtHeaderConverter()
    private val jsonParser = JsonParserFactory.create()

    override fun decode(token: String): Map<String, Any> {
        val headers = jwtHeaderConverter.convert(token)
        val keyIdHeader = headers[KEY_ID]
                ?: throw InvalidTokenException("Invalid JWT/JWS: $KEY_ID is a required JOSE Header")
        val jwkDefinitionHolder = jwkDefinitionSource.getDefinitionLoadIfNecessary(keyIdHeader)
                ?: throw InvalidTokenException("Invalid JOSE Header $KEY_ID ($keyIdHeader)")
        val jwkDefinition = jwkDefinitionHolder.jwkDefinition
        val algorithmHeader = headers[ALGORITHM]
                ?: throw InvalidTokenException("Invalid JWT/JWS: $ALGORITHM is a required JOSE Header")
        if (jwkDefinition.algorithm != null && algorithmHeader != jwkDefinition.algorithm.headerParamValue()) {
            throw InvalidTokenException("Invalid JOSE Header $ALGORITHM ($algorithmHeader) does not match algorithm associated to JWK with $KEY_ID ($keyIdHeader)")
        }
        return JwtHelper.decode(token)
                .also { it.verifySignature(jwkDefinitionHolder.signatureVerifier) }
                .let { jsonParser.parseMap(it.claims) }
                .mapValues { (key, value) -> if(key == EXP && value is Int) value as Long else value }
                .also(jwtClaimsSetVerifier::verify)
    }

    override fun encode(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): String =
            throw JwkException("JWT signing (JWS) is not supported.")
}