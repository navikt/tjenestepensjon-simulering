package no.nav.tjenestepensjon.simulering.config;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final Map<String, String> issuerJwksMap;
    private final JwtClaimsSetVerifier claimsSetVerifier;

    public ResourceServerConfig(Map<String, String> issuerJwksMap, JwtClaimsSetVerifier claimsSetVerifier) {
        this.issuerJwksMap = issuerJwksMap;
        this.claimsSetVerifier = claimsSetVerifier;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(new JwkTokenStore(new ArrayList<>(issuerJwksMap.values()), null, claimsSetVerifier));
        resources.resourceId(null); //Avoid audience-claim check.
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/isAlive").permitAll()
                .antMatchers("/isReady").permitAll()
                .anyRequest().authenticated();
    }
}
