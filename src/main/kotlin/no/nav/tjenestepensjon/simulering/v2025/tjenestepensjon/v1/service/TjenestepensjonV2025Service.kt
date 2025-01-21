package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningDto
import no.nav.tjenestepensjon.simulering.ping.PingResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjonMedMaanedsUtbetalinger
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.exception.BrukerErIkkeMedlemException
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
    private val tpOrdningNavn = mapOf(
        "3010" to "spk",
        "4080" to "klp",
        "3200" to "klp",
    )

    fun simuler(request: SimulerTjenestepensjonRequestDto): Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> {
        val tpOrdninger = try {
            tp.findTPForhold(request.pid)
        }
        catch (e: TpregisteretException) {
            return emptyList<String>() to Result.failure(e)
        }

        val tpOrdningerNavn = tpOrdninger.map { it.navn }
        if (tpOrdningerNavn.isEmpty()) {
            return emptyList<String>() to Result.failure(BrukerErIkkeMedlemException())
        }

        val sisteTpOrdningNavn = finnSisteTpOrdningService.finnSisteOrdning(tpOrdninger)
        log.info { "Fant aktive tp-ordninger for bruker: $tpOrdninger, skal bruke $sisteTpOrdningNavn for å simulere" }
        try {
            simulerv2(request, tpOrdninger)
        } catch (e: Exception) { //Midlertidig for test
            log.info { "Feil ved simulering av tjenestepensjon i v2" }
        }

        return when (sisteTpOrdningNavn.lowercase()) {
            "spk" -> tpOrdningerNavn to spk.simuler(request,"3010")
            "klp" -> tpOrdningerNavn to klp.simuler(request)
            else -> tpOrdningerNavn to Result.failure(TpOrdningStoettesIkkeException(sisteTpOrdningNavn))
        }
    }

    private fun simulerv2(request: SimulerTjenestepensjonRequestDto, tpOrdninger: List<TpOrdningDto>): Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> {
        val sisteOrdningerNr = finnSisteTpOrdningService.finnSisteOrdningKandidater(tpOrdninger)
        val sisteOrdningerNavn = sisteOrdningerNr.mapNotNull { tpOrdningNavn[it] }

        val simulertTpListe = sisteOrdningerNr.map { ordning ->
            when (tpOrdningNavn[ordning]) {
                "spk" ->  spk.simuler(request, ordning)
                "klp" -> klp.simulerv2(request, ordning)
                else -> Result.failure(TpOrdningStoettesIkkeException(ordning))
            }.run {
                onSuccess { if (it.utbetalingsperioder.isNotEmpty()) return sisteOrdningerNavn to this }
            }
        }
        simulertTpListe.forEach { it -> it.onFailure { e -> return sisteOrdningerNavn to Result.failure(e) } }

        return emptyList<String>() to Result.failure(TpOrdningStoettesIkkeException("Ingen støttede tjenestepensjonsordninger"))
    }

    fun ping(): List<PingResponse> {
        return listOf(
            spk.ping(),
            klp.ping(),
            tp.ping())
    }
}