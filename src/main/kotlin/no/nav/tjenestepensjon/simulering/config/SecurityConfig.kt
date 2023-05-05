package no.nav.tjenestepensjon.simulering.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${simulering.security.issuers}")
    issuers: String
) {
    private val issuerResolver = JwtIssuerAuthenticationManagerResolver(issuers.split(','))

    @Bean
    fun configure(http: HttpSecurity) = http.run {
        csrf().disable()
        authorizeHttpRequests()
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        oauth2ResourceServer {
            it.authenticationManagerResolver(issuerResolver)
        }
        build()
    }
}
