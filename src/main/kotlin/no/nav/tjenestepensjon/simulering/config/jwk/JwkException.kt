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

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception

/**
 * General exception for JSON Web Key (JWK) related errors.
 *
 * @author Joe Grandja
 */
class JwkException : OAuth2Exception {
    private val errorCode = SERVER_ERROR_ERROR_CODE
    private val httpStatus = 500

    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * Returns the `error` used in the *OAuth2 Error Response*
     * sent back to the caller. The default is &quot;server_error&quot;.
     *
     * @return the `error` used in the *OAuth2 Error Response*
     */
    override fun getOAuth2ErrorCode() = errorCode

    /**
     * Returns the Http Status used in the *OAuth2 Error Response*
     * sent back to the caller. The default is 500.
     *
     * @return the `Http Status` set on the *OAuth2 Error Response*
     */
    override fun getHttpErrorCode() = httpStatus

    companion object {
        private const val SERVER_ERROR_ERROR_CODE = "server_error"
    }
}