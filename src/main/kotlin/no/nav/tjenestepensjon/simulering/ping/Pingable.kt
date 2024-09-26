package no.nav.tjenestepensjon.simulering.ping

interface Pingable {
    fun ping(): PingResponse
}