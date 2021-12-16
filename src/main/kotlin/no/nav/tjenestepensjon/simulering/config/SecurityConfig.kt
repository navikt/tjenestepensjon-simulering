package no.nav.tjenestepensjon.simulering.config

import com.nimbusds.jwt.JWTParser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${simulering.security.issuers}")
    issuers: String
) : WebSecurityConfigurerAdapter() {
    private val LOG = LoggerFactory.getLogger(javaClass)

    private val issuerResolver = JwtIssuerAuthenticationManagerResolver(issuers.split(','))

    override fun configure(http: HttpSecurity) {
        http.run {
            csrf().disable()
            authorizeRequests()
                .requestMatchers(RequestMatcher {
                    if ("Authorization" in it.headerNames.toList()) {
                        try {
                            val token = it.getHeader("Authorization").removePrefix("Bearer ")
                            val issuer = JWTParser.parse(token).jwtClaimsSet.issuer
                            LOG.info("Received call validated with issuer: $issuer")
                        } catch (_: Exception) {
                            /*Do nothing*/
                        }
                    }
                    false
                }).denyAll()
                .antMatchers("/isAlive", "/isReady", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            oauth2ResourceServer {
                it.authenticationManagerResolver(issuerResolver)
            }
        }
    }
}