package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.config.TpLeverandorConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(TpLeverandorConfig.TpLeverandorProperty::class)
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class TjenestepensjonSimuleringApplication{
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<TjenestepensjonSimuleringApplication>(*args)
        }
    }
}
