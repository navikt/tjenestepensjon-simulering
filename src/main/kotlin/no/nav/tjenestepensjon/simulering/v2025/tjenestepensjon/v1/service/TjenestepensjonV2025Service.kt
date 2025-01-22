package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TjenestepensjonSimuleringException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpOrdningStoettesIkkeException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.TpregisteretException
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPTjenestepensjonService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKTjenestepensjonService
import org.springframework.stereotype.Service

@Service
class TjenestepensjonV2025Service(
    private val tp: TpClient,
    private val spk: SPKTjenestepensjonService,
    private val klp: KLPTjenestepensjonService,
    private val finnSisteTpOrdningService: FinnSisteTpOrdningService,
    ) {
    private val log = KotlinLogging.logger {}

    fun simuler(request: SimulerTjenestepensjonRequestDto): Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> {
        val tpOrdninger = try {
            tp.findTPForhold(request.pid)
        }
        catch (e: TpregisteretException) {
            return emptyList<String>() to Result.failure(e)
        }


        val tpOrdningerNavn = tpOrdninger.map { it.navn }
        val sisteOrdningerNr = finnSisteTpOrdningService.finnSisteOrdningKandidater(tpOrdninger)
        if (sisteOrdningerNr.isEmpty()) {
            return emptyList<String>() to Result.failure(BrukerErIkkeMedlemException())
        }

        val simulertTpListe = sisteOrdningerNr.map { ordning ->
            when (ordning) {
                "3010" -> spk.simuler(request, "3010") //3010 -> TpNummer for SPK
                "3060" -> spk.simuler(request, "3060") //3060 -> TpNummer for SPK
                "4080" -> klp.simuler(request, "4080") //4080 -> TpNummer for KLP
                "3200" -> klp.simuler(request, "3200") //3200 -> TpNummer for KLP
                else -> Result.failure(TpOrdningStoettesIkkeException(ordning))
            }.also { log.info { "Respons fra simulering: ${it.getOrNull()?.serviceData}" } }.run {
                onSuccess { if (it.utbetalingsperioder.isNotEmpty()) return tpOrdningerNavn to this }
            }
        }
        return tpOrdningerNavn to (simulertTpListe.firstOrNull { it.isFailure } ?: Result.failure(TjenestepensjonSimuleringException("Ingen utbetalingsperioder")))

    }

    fun ping(): List<PingResponse> {
        return listOf(
            spk.ping(),
            klp.ping(),
            tp.ping())
    }
}