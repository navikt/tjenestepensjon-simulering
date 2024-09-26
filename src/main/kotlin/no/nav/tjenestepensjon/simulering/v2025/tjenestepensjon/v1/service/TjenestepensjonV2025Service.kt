package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPTjenestepensjonClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKTjenestepensjonClient
import org.springframework.stereotype.Service

@Service
class TjenestepensjonV2025Service(
    private val tp: TpClient,
    private val spk: SPKTjenestepensjonClient,
    private val klp: KLPTjenestepensjonClient,
    ) {

    @Throws(BrukerErIkkeMedlemException::class, TpOrdningStoettesIkkeException::class)
    fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjon> {
        val tpOrdningNavn = tp.findTPForhold(request.fnr).flatMap { it.alias }.firstOrNull()
            ?: "spk" //TODO throw BrukerErIkkeMedlemException()

        return when (tpOrdningNavn.lowercase()) {
            "spk" -> spk.simuler(request)
            "klp" -> klp.simuler(request)
            else -> throw TpOrdningStoettesIkkeException(tpOrdningNavn)
        }
    }

    fun ping(): List<PingResponse> {
        return listOf(
            spk.ping(),
            klp.ping(),
            tp.ping())
    }
}