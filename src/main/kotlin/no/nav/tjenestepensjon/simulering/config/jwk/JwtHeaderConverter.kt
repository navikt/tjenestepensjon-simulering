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

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import org.springframework.core.convert.converter.Converter
import org.springframework.security.jwt.codec.Codecs
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import java.io.IOException
import java.util.*

/**
 * A [Converter] that converts the supplied `String` representation of a JWT
 * to a `Map` of JWT Header Parameters.
 *
 * @see [JSON Web Token
 * @author Joe Grandja
 * @author Vedran Pavic
](https://tools.ietf.org/html/rfc7519) */
internal class JwtHeaderConverter : Converter<String, Map<String, String>> {
    private val factory = JsonFactory()
    /**
     * Converts the supplied JSON Web Token to a `Map` of JWT Header Parameters.
     *
     * @param token the JSON Web Token
     * @return a `Map` of JWT Header Parameters
     * @throws InvalidTokenException if the JWT is invalid
     */
    override fun convert(token: String): Map<String, String> {
        val headers: MutableMap<String, String>
        val headerEndIndex = token.indexOf('.')
        if (headerEndIndex == -1) {
            throw InvalidTokenException("Invalid JWT. Missing JOSE Header.")
        }
        val decodedHeader: ByteArray
        decodedHeader = try {
            Codecs.b64UrlDecode(token.substring(0, headerEndIndex))
        } catch (ex: IllegalArgumentException) {
            throw InvalidTokenException("Invalid JWT. Malformed JOSE Header.", ex)
        }
        var parser: JsonParser? = null
        try {
            parser = factory.createParser(decodedHeader)
            headers = HashMap()
            if (parser.nextToken() == JsonToken.START_OBJECT) {
                while (parser.nextToken() == JsonToken.FIELD_NAME) {
                    val headerName = parser.currentName
                    parser.nextToken()
                    val headerValue = parser.valueAsString
                    headers[headerName] = headerValue
                }
            }
        } catch (ex: IOException) {
            throw InvalidTokenException("An I/O error occurred while reading the JWT: " + ex.message, ex)
        } finally {
            try {
                parser?.close()
            } catch (ex: IOException) {
            }
        }
        return headers
    }
}