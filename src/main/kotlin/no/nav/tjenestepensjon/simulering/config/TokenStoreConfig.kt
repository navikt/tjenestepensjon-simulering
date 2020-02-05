package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore

@Configuration
class TokenStoreConfig(private val tokenProviders: List<TokenProvider>, private val claimsSetVerifier: JwtClaimsSetVerifier) {
    @Bean
    fun tokenStore(): TokenStore = JwkTokenStore(tokenProviders.map(TokenProvider::jwksUrl), jwtAccessTokenConverter(), claimsSetVerifier)

    @Bean
    fun jwtAccessTokenConverter() = JwtAccessTokenConverter().apply {
        jwtClaimsSetVerifier = claimsSetVerifier
    }
}