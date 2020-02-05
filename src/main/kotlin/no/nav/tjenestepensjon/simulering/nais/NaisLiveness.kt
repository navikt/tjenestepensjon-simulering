package no.nav.tjenestepensjon.simulering.nais

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NaisLiveness {
    @get:GetMapping("/isAlive")
    val isAlive = OK

    @get:GetMapping("/isReady")
    val isReady = OK
}