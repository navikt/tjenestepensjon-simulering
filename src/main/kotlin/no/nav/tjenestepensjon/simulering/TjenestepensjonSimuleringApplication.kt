package no.nav.tjenestepensjon.simulering

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationPropertiesScan
object TjenestepensjonSimuleringApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        runApplication<TjenestepensjonSimuleringApplication>(*args)
    }
}