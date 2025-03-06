package no.nav.tjenestepensjon.simulering

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class TjenestepensjonSimuleringApplication{
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<TjenestepensjonSimuleringApplication>(*args)
        }
    }
}
