package no.nav.tjenestepensjon.simulering.ping

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

class PingResponse(
    val provider: String,
    val tjeneste: String,
    val melding: String,
    @param:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()){
    override fun toString(): String {
        return "PingResponse(provider='$provider', tjeneste='$tjeneste, timestamp=$timestamp, melding='$melding')"
    }
}