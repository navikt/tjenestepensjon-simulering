package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto

object SPKMapper {

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto) =
        SPKSimulerTjenestepensjonRequest(
            personId = request.fnr,
            uttaksListe = request.uttaksListe.map { uttakDto ->
                Uttak(
                    ytelseType = uttakDto.ytelseType,
                    fraOgMedDato = uttakDto.fraOgMedDato,
                    uttaksgrad = uttakDto.uttaksgrad
                )
            },
            fremtidigInntektListe = request.fremtidigInntektListe.map { fremtidigInntektDto ->
                FremtidigInntekt(
                    fraOgMedDato = fremtidigInntektDto.fraOgMedDato,
                    aarligInntekt = fremtidigInntektDto.aarligInntekt
                )
            },
            aarIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )

    fun mapToResponse(response: SPKSimulerTjenestepensjonResponse) =
        SimulertTjenestepensjon(
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingListe.flatMap { periode ->
                val fraOgMed = periode.fraOgMedDato
                periode.delytelseListe.map { Utbetalingsperiode(fraOgMed, it.maanedligBelop, it.ytelseType) }
            },
            aarsakIngenUtbetaling = response.aarsakIngenUtbetaling.map { it.statusBeskrivelse + ": " + it.ytelseType }
        )
}