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
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AccessTokenConverter
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore

/**
 * A [TokenStore] implementation that provides support for verifying the
 * JSON Web Signature (JWS) for a JSON Web Token (JWT) using a JSON Web Key (JWK).
 * <br></br>
 * <br></br>
 * This [TokenStore] implementation is **exclusively** meant to be used by a **Resource Server** as
 * it's sole responsibility is to decode a JWT and verify it's signature (JWS) using the corresponding JWK.
 * <br></br>
 * <br></br>
 * **NOTE:**
 * There are a few operations defined by [TokenStore] that are not applicable for a Resource Server.
 * In these cases, the method implementation will explicitly throw a
 * [JwkException] reporting *&quot;This operation is not supported&quot;*.
 * <br></br>
 * <br></br>
 * The unsupported operations are as follows:
 *
 *  * [.storeAccessToken]
 *  * [.removeAccessToken]
 *  * [.storeRefreshToken]
 *  * [.readRefreshToken]
 *  * [.readAuthenticationForRefreshToken]
 *  * [.removeRefreshToken]
 *  * [.removeAccessTokenUsingRefreshToken]
 *  * [.getAccessToken]
 *  * [.findTokensByClientIdAndUserName]
 *  * [.findTokensByClientId]
 *
 * <br></br>
 * This implementation delegates to an internal instance of a [JwtTokenStore] which uses a
 * specialized extension of [JwtAccessTokenConverter].
 * This specialized [JwtAccessTokenConverter] is capable of fetching (and caching)
 * the JWK Set (a set of JWKs) from the URL supplied to the constructor of this implementation.
 * <br></br>
 * <br></br>
 * The [JwtAccessTokenConverter] will verify the JWS in the following step sequence:
 * <br></br>
 * <br></br>
 *
 *  1. Extract the **&quot;kid&quot;** parameter from the JWT header.
 *  1. Find the matching JWK with the corresponding **&quot;kid&quot;** attribute.
 *  1. Obtain the `SignatureVerifier` associated with the JWK and verify the signature.
 *
 * <br></br>
 * **NOTE:** The algorithms currently supported by this implementation are: RS256, RS384 and RS512.
 * <br></br>
 * <br></br>
 *
 * @author Joe Grandja
 * @see JwtTokenStore
 *
 * @see [JSON Web Key
 * @see [JSON Web Token
 * @see [JSON Web Signature
](https://tools.ietf.org/html/rfc7515)](https://tools.ietf.org/html/rfc7519)](https://tools.ietf.org/html/rfc7517) */
class JwkTokenStore(tokenProviders: List<TokenProvider>, accessTokenConverter: AccessTokenConverter?, jwtClaimsSetVerifier: JwtClaimsSetVerifier?) : TokenStore {
    private val delegate: TokenStore
    /**
     * Delegates to the internal instance [JwtTokenStore.readAuthentication].
     *
     * @param token the access token
     * @return the [OAuth2Authentication] representation of the access token
     */
    override fun readAuthentication(token: OAuth2AccessToken): OAuth2Authentication {
        return delegate.readAuthentication(token)
    }

    /**
     * Delegates to the internal instance [JwtTokenStore.readAuthentication].
     *
     * @param tokenValue the access token value
     * @return the [OAuth2Authentication] representation of the access token
     */
    override fun readAuthentication(tokenValue: String): OAuth2Authentication {
        return delegate.readAuthentication(tokenValue)
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun storeAccessToken(token: OAuth2AccessToken, authentication: OAuth2Authentication) {
        throw operationNotSupported()
    }

    /**
     * Delegates to the internal instance [JwtTokenStore.readAccessToken].
     *
     * @param tokenValue the access token value
     * @return the [OAuth2AccessToken] representation of the access token value
     */
    override fun readAccessToken(tokenValue: String): OAuth2AccessToken {
        return delegate.readAccessToken(tokenValue)
    }

    /**
     * Delegates to the internal instance [JwtTokenStore.removeAccessToken].
     *
     * @param token the access token
     */
    override fun removeAccessToken(token: OAuth2AccessToken) {
        delegate.removeAccessToken(token)
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun storeRefreshToken(refreshToken: OAuth2RefreshToken, authentication: OAuth2Authentication) {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun readRefreshToken(tokenValue: String): OAuth2RefreshToken {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun readAuthenticationForRefreshToken(token: OAuth2RefreshToken): OAuth2Authentication {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun removeRefreshToken(token: OAuth2RefreshToken) {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun removeAccessTokenUsingRefreshToken(refreshToken: OAuth2RefreshToken) {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun getAccessToken(authentication: OAuth2Authentication): OAuth2AccessToken {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun findTokensByClientIdAndUserName(clientId: String, userName: String): Collection<OAuth2AccessToken> {
        throw operationNotSupported()
    }

    /**
     * This operation is not applicable for a Resource Server
     * and if called, will throw a [JwkException].
     *
     * @throws JwkException reporting this operation is not supported
     */
    override fun findTokensByClientId(clientId: String): Collection<OAuth2AccessToken> {
        throw operationNotSupported()
    }

    private fun operationNotSupported(): JwkException {
        return JwkException("This operation is not supported.")
    }

    /**
     * Creates a new instance using the provided [TokenProvider] as the location for the JWK Sets
     * and a custom [AccessTokenConverter] and [JwtClaimsSetVerifier].
     *
     * @param tokenProviders the JWK Set URLs
     * @param accessTokenConverter a custom [AccessTokenConverter]
     * @param jwtClaimsSetVerifier a custom [JwtClaimsSetVerifier]
     */
    init {
        val jwkDefinitionSource = JwkDefinitionSource(tokenProviders)
        val jwtVerifyingAccessTokenConverter = JwkVerifyingJwtAccessTokenConverter(jwkDefinitionSource)
        if (accessTokenConverter != null) {
            jwtVerifyingAccessTokenConverter.accessTokenConverter = accessTokenConverter
        }
        if (jwtClaimsSetVerifier != null) {
            jwtVerifyingAccessTokenConverter.jwtClaimsSetVerifier = jwtClaimsSetVerifier
        }
        delegate = JwtTokenStore(jwtVerifyingAccessTokenConverter)
    }
}