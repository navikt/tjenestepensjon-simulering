package no.nav.tjenestepensjon.simulering.config;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

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

    private final List<URL> jwksUrls;
    private final JwtClaimsSetVerifier claimsSetVerifier;

    public ResourceServerConfig(List<URL> jwksUrls, JwtClaimsSetVerifier claimsSetVerifier) {
        this.jwksUrls = jwksUrls;
        this.claimsSetVerifier = claimsSetVerifier;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        List<String> jwksUrls = this.jwksUrls.stream().map(URL::toString).collect(Collectors.toList());
        resources.tokenStore(new JwkTokenStore(jwksUrls, null, claimsSetVerifier));
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
