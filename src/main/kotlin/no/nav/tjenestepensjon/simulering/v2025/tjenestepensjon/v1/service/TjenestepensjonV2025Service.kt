package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.TjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TPOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class TjenestepensjonV2025Service(
    private val tpClient: TpClient,
    @Qualifier("spk") private val spk: TjenestepensjonV2025Client,
    @Qualifier("klp") private val klp: TjenestepensjonV2025Client,
    ) {

    @Throws(BrukerErIkkeMedlemException::class, TPOrdningStoettesIkkeException::class, TjenestepensjonSimuleringException::class)
    fun simuler(request: SimulerTjenestepensjonRequestDto): SimulertTjenestepensjon {
        val tpOrdningNavn = tpClient.findTPForhold(request.fnr).flatMap { it.alias }.firstOrNull()
            ?: throw BrukerErIkkeMedlemException()

        return when (tpOrdningNavn.lowercase()) {
            "spk" -> spk.simuler(TjenestepensjonRequest(request.fnr))
            "klp" -> klp.simuler(TjenestepensjonRequest(request.fnr))
            else -> throw TPOrdningStoettesIkkeException(tpOrdningNavn)
        }
    }
}