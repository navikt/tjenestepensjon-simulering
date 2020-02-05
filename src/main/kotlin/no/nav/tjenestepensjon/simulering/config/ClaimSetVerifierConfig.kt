package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.token.store.DelegatingJwtClaimsSetVerifier
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier
import org.springframework.util.Assert

@Configuration
class ClaimSetVerifierConfig(private val tokenProviders: List<TokenProvider>) {
    @Bean
    fun claimsSetVerifier() =
            DelegatingJwtClaimsSetVerifier(listOf(IssuerClaimVerifier(tokenProviders.map(TokenProvider::issuer).toSet())))

    internal class IssuerClaimVerifier(private val issuerUrls: Set<String>) : JwtClaimsSetVerifier {

        override fun verify(claims: Map<String, Any>) {
            claims[ISS_CLAIM]?.toString()?.takeUnless(issuerUrls::contains).let {
                throw InvalidTokenException("Invalid issuer (iss) claim: $it")
            }
        }

        companion object {
            private const val ISS_CLAIM = "iss"
        }

        init {
            Assert.notNull(issuerUrls, "issuerUrls cannot be null")
            Assert.notEmpty(issuerUrls, "issuerUrls cannot be empty")
        }
    }

}