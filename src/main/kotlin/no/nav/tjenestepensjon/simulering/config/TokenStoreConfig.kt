package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import no.nav.tjenestepensjon.simulering.config.jwk.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier

@Configuration
class TokenStoreConfig(private val tokenProviders: List<TokenProvider>, private val claimsSetVerifier: JwtClaimsSetVerifier) {
    @Bean
    fun tokenStore(): TokenStore = JwkTokenStore(tokenProviders, jwtAccessTokenConverter(), claimsSetVerifier)

    @Bean
    fun jwtAccessTokenConverter() =
            JwkVerifyingJwtAccessTokenConverter(JwkDefinitionSource(tokenProviders))

}