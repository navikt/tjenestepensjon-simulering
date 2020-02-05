package no.nav.tjenestepensjon.simulering

import org.springframework.boot.runApplication

object TjenestepensjonSimuleringApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        runApplication<TjenestepensjonSimuleringApplication>(*args)
    }
}