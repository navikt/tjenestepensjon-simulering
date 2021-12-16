package no.nav.tjenestepensjon.simulering.rest

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/*
Pesys needs these. Should be replaced with /actuator/health/ endpoints as soon as possible.
 */
@RestController
class PesysHealthProbes {
    @get:GetMapping("/isAlive")
    val isAlive = OK

    @get:GetMapping("/isReady")
    val isReady = OK
}