package no.nav.tjenestepensjon.simulering.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

@Configuration
@EnableResourceServer
class ResourceServerConfig(private val tokenStore: TokenStore) : ResourceServerConfigurerAdapter() {
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenStore(tokenStore)
        resources.resourceId(null) //Avoid audience-claim check.
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/isAlive", "/isReady", "/actuator/**").permitAll()
                .anyRequest().authenticated()
    }

}