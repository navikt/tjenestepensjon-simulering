package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.*
import java.time.LocalDate

object SPKMapper {
    private val log = KotlinLogging.logger {}

    const val PROVIDER_FULLT_NAVN = "Statens Pensjonskasse"

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto) =
        SPKSimulerTjenestepensjonRequest(
            personId = request.pid,
            uttaksListe = opprettUttaksliste(request),
            fremtidigInntektListe = listOf(
                FremtidigInntekt(
                    fraOgMedDato = fjorAarSomManglerOpptjeningIPopp(),
                    aarligInntekt = request.sisteInntekt
                )
            ),
            aarIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )

    private fun fjorAarSomManglerOpptjeningIPopp(): LocalDate = LocalDate.now().minusYears(1).withDayOfYear(1)

    fun mapToResponse(response: SPKSimulerTjenestepensjonResponse): SimulertTjenestepensjon {
        log.debug { "Mapping response from SPK $response" }
        return SimulertTjenestepensjon(
            tpLeverandoer = PROVIDER_FULLT_NAVN,
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingListe.flatMap { periode ->
                val fraOgMed = periode.fraOgMedDato
                periode.delytelseListe.map { Utbetalingsperiode(fraOgMed, it.maanedligBelop, it.ytelseType) }
            },
            aarsakIngenUtbetaling = response.aarsakIngenUtbetaling.map { it.statusBeskrivelse + ": " + it.ytelseType },
            betingetTjenestepensjonErInkludert = response.utbetalingListe.flatMap { it.delytelseListe }.any { it.ytelseType == "BTP" }
        )
    }

    fun opprettUttaksliste(request: SimulerTjenestepensjonRequestDto): List<Uttak> {
        return SPKYtelse.hentAlleUnntattType(if (request.brukerBaOmAfp) SPKYtelse.BTP else SPKYtelse.OAFP)
            .map {
                Uttak(
                    ytelseType = it,
                    fraOgMedDato = request.uttaksdato,
                    uttaksgrad = null
                )
            }
    }
}