package no.nav.tjenestepensjon.simulering.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${simulering.security.issuers}")
    issuers: String
) {
    private val issuerResolver = fromTrustedIssuers(issuers.split(','))

    @Bean
    fun configure(http: HttpSecurity): DefaultSecurityFilterChain = http.run {
        csrf { it.disable() }
        authorizeHttpRequests {
            it.requestMatchers("/actuator/**","/v2025/tjenestepensjon/ping","/simulering/ping").permitAll().anyRequest().authenticated()
        }
        oauth2ResourceServer {
            it.authenticationManagerResolver(issuerResolver)
        }
        build()
    }
}
