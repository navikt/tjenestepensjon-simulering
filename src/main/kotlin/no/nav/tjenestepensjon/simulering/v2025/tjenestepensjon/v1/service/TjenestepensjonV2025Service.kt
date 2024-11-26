package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
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
    ) {
    private val log = KotlinLogging.logger {}

    fun simuler(request: SimulerTjenestepensjonRequestDto): Pair<List<String>, Result<SimulertTjenestepensjonMedMaanedsUtbetalinger>> {
        val tpOrdninger = try {
            tp.findTPForhold(request.pid)
        }
        catch (e: TpregisteretException) {
            return emptyList<String>() to Result.failure(e)
        }

        val sisteTpOrdningNavn = tpOrdninger.flatMap { it.alias }.firstOrNull() //avventer siste ordning fra SPK

        if (sisteTpOrdningNavn == null) {
            return emptyList<String>() to Result.failure(BrukerErIkkeMedlemException())
        }

        log.info { "Fant aktive tp-ordninger for bruker: $tpOrdninger, skal bruke $sisteTpOrdningNavn for å simulere" }

        val tpOrdningerNavn = tpOrdninger.map { it.navn }

        return when (sisteTpOrdningNavn.lowercase()) {
            "spk" -> tpOrdningerNavn to spk.simuler(request)
            //"klp" -> tpOrdningerNavn to klp.simuler(request)
            else -> tpOrdningerNavn to Result.failure(TpOrdningStoettesIkkeException(sisteTpOrdningNavn))
        }
    }

    fun ping(): List<PingResponse> {
        return listOf(
            spk.ping(),
            klp.ping(),
            tp.ping())
    }
}