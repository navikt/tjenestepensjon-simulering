package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier
import org.springframework.security.oauth2.provider.token.store.jwk.*
import java.io.IOException
import java.net.*
import java.net.Proxy.Type.HTTP

@Configuration
class TokenStoreConfig(private val tokenProviders: List<TokenProvider>, private val claimsSetVerifier: JwtClaimsSetVerifier) {

    @Bean
    fun tokenStore(): TokenStore =
            JwkTokenStore(tokenProviders.map(TokenProvider::jwksUrl), jwtAccessTokenConverter(), claimsSetVerifier)

    @Bean
    fun jwtAccessTokenConverter() = JwtAccessTokenConverter().apply {
        jwtClaimsSetVerifier = claimsSetVerifier
    }

    init {
        ProxySelector.setDefault(JwkProxySelector(tokenProviders))
    }

    class JwkProxySelector(tokenProviders: List<TokenProvider>): ProxySelector() {
        private val defaultProxySelector: ProxySelector = getDefault()
        private val proxyMap = tokenProviders.associateBy(TokenProvider::jwksUrl)
                .mapValues { (_, provider) ->
                    provider.proxyUrl?.split(':')
                            ?.let { InetSocketAddress(it[0], it[1].toInt()) }
                            ?.let { Proxy(HTTP, it) }
                }

        override fun select(p0: URI?): MutableList<Proxy> =
                proxyMap[p0.toString()]?.let { mutableListOf(it) } ?: defaultProxySelector.select(p0)

        override fun connectFailed(p0: URI?, p1: SocketAddress?, p2: IOException?) =
                defaultProxySelector.connectFailed(p0, p1, p2)
    }
}