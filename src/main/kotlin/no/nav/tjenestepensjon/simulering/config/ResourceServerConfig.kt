package no.nav.tjenestepensjon.simulering.config

import com.nimbusds.jwt.JWTParser
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableResourceServer
class ResourceServerConfig(private val tokenStore: TokenStore) : ResourceServerConfigurerAdapter() {
    private val LOG = LoggerFactory.getLogger(javaClass)
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenStore(tokenStore)
        resources.resourceId(null) //Avoid audience-claim check.
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .requestMatchers(RequestMatcher {
                if ("Authorization" in it.headerNames.toList()) {
                    val token = it.getHeader("Authorization").removePrefix("Bearer ")
                    val issuer = JWTParser.parse(token).jwtClaimsSet.issuer
                    LOG.info("Received call validated with issuer: $issuer")
                }
                false
            }).denyAll()
            .antMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
    }
}