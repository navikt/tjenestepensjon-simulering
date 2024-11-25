package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import java.time.LocalDate

object KLPMapper {

    private const val LEVERANDOER = "Kommunal landspensjonskasse"

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto) =
        KLPSimulerTjenestepensjonRequest(
            personId = request.pid,
            uttaksListe = listOf(
                Uttak(
                    ytelseType = "ALLE",
                    fraOgMedDato = request.uttaksdato,
                    uttaksgrad = 100
                )
            ),
            fremtidigInntektsListe = listOf(
                FremtidigInntekt(
                    fraOgMedDato = aarUtenRegistrertInntektHosSkatteetaten(),
                    arligInntekt = request.sisteInntekt
                )
            ),
            arIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )

    private fun aarUtenRegistrertInntektHosSkatteetaten(): LocalDate = LocalDate.now().minusYears(2).withDayOfYear(1)

    fun mapToResponse(response: KLPSimulerTjenestepensjonResponse) =
        SimulertTjenestepensjon(
            tpLeverandoer = LEVERANDOER,
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingsListe.map { Utbetalingsperiode(it.fraOgMedDato, it.manedligUtbetaling, it.ytelseType) },
            aarsakIngenUtbetaling = response.arsakIngenUtbetaling,
            betingetTjenestepensjonErInkludert = response.utbetalingsListe.any { it.ytelseType == "BTP" }
        )
}