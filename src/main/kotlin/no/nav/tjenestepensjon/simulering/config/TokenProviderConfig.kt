package no.nav.tjenestepensjon.simulering.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenProviderConfig(
    @Value("\${AZURE_OPENID_CONFIG_ISSUER:#{null}}")
    private val azureIssuer: String?,
    @Value("\${AZURE_OPENID_CONFIG_JWKS_URI:#{null}}")
    private val azureJwksUri: String?
) {
    @Value("\${ISSUER_JWKS_MAP}")
    lateinit var issuerJwksMap: String

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
        }.let {
            if(azureIssuer != null && azureJwksUri != null)
                it + TokenProvider(azureIssuer, azureJwksUri, "webproxy-nais.nav.no:8080")
            else it
        }
    }
    private fun validateProxyUrl(proxyUrl: String) {
        if (proxyUrl.count(':'::equals) != 1)
            throw IllegalStateException("Proxy configuration requires both host and port!")
    }

    class TokenProvider @JvmOverloads constructor(val issuer: String, val jwksUrl: String, val proxyUrl: String? = null)
}