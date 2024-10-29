package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import java.time.LocalDate

object SPKMapper {

    const val LEVERANDOER = "Statens pensjonskasse"

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto) =
        SPKSimulerTjenestepensjonRequest(
            personId = request.pid,
            uttaksListe = opprettUttaksliste(request),
            fremtidigInntektListe = listOf(
                FremtidigInntekt(
                    fraOgMedDato = LocalDate.now().withDayOfYear(1),
                    aarligInntekt = request.sisteInntekt
                )
            ),
            aarIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )

    fun mapToResponse(response: SPKSimulerTjenestepensjonResponse) =
        SimulertTjenestepensjon(
            tpLeverandoer = LEVERANDOER,
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingListe.flatMap { periode ->
                val fraOgMed = periode.fraOgMedDato
                periode.delytelseListe.map { Utbetalingsperiode(fraOgMed, it.maanedligBelop, it.ytelseType) }
            },
            aarsakIngenUtbetaling = response.aarsakIngenUtbetaling.map { it.statusBeskrivelse + ": " + it.ytelseType }
        )

    fun opprettUttaksliste(request: SimulerTjenestepensjonRequestDto) =
        mutableListOf("PAASLAG", "APOF2020", "OAFP", "OT6370", "SAERALDERSPAASLAG", if (request.brukerBaOmAfp) "AFP" else "BTP")
            .map {
                Uttak(
                    ytelseType = it,
                    fraOgMedDato = request.uttaksdato,
                    uttaksgrad = null
                )
            }
}