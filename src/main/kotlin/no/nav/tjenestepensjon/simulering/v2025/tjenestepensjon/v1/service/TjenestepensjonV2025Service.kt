package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class TjenestepensjonV2025Service(
    private val tpClient: TpClient,
    @Qualifier("oftp-2025-spk") private val spk: TjenestepensjonV2025Client,
    @Qualifier("oftp-2025-klp") private val klp: TjenestepensjonV2025Client,
    ) {

    @Throws(BrukerErIkkeMedlemException::class, TpOrdningStoettesIkkeException::class)
    fun simuler(request: SimulerTjenestepensjonRequestDto): Result<SimulertTjenestepensjon> {
        val tpOrdningNavn = tpClient.findTPForhold(request.fnr).flatMap { it.alias }.firstOrNull()
            ?: throw BrukerErIkkeMedlemException()

        return when (tpOrdningNavn.lowercase()) {
            "spk" -> spk.simuler(request)
            "klp" -> klp.simuler(request)
            else -> klp.simuler(request) //TODO throw TpOrdningStoettesIkkeException(tpOrdningNavn)
        }
    }
}