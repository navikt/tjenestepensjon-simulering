package no.nav.tjenestepensjon.simulering

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TjenestepensjonSimuleringApplication{
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<TjenestepensjonSimuleringApplication>(*args)
        }
    }
}

