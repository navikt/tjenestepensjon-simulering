package no.nav.tjenestepensjon.simulering.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenProviderConfig {
    private lateinit var issuerJwksMap: String

    @Value("\${ISSUER_JWKS_MAP}")
    fun setIssuerJwksMap(jwksMap: String) {
        issuerJwksMap = jwksMap
    }

    /**
     * Parse env variable to generate a list of TokenProviders.
     * "," delimits the details of induvidual providers
     * "|" delimits different providers
     * Format of env variable: "issuer-url (reqiured),issuer-jwks-url (required), proxy-url (optional)"
     *
     * @return List of TokenProvider
     */
    @Bean
    fun createFromEnv(): List<TokenProvider> {
        return issuerJwksMap.split('|').map { s ->
            val data = s.split(',')
            assert(data.size == 2 || data.size == 3)
            if (data.size == 2) {
                assert(data[0].isNotEmpty() && data[1].isNotEmpty())
                TokenProvider(data[0], data[1])
            } else {
                assert(data[0].isNotEmpty() && data[1].isNotEmpty() && data[2].isNotEmpty())
                validateProxyUrl(data[2])
                TokenProvider(data[0], data[1], data[2])
            }
        }
    }
    private fun validateProxyUrl(proxyUrl: String) {
        if (proxyUrl.count(':'::equals) != 2)
            throw IllegalStateException("Proxy configuration requires both host and port!")
    }

    class TokenProvider @JvmOverloads constructor(val issuer: String, val jwksUrl: String, val proxyUrl: String? = null)
}