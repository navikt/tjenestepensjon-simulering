package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator

data class SimulertAP2011 @JsonCreator constructor(
        val simulertForsteuttak: Simuleringsdata,
        val simulertHeltUttakEtter67Ar: Simuleringsdata? = null
)