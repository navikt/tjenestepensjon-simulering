package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto

object KLPMapper {

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto) =
        KLPSimulerTjenestepensjonRequest(
            personId = request.fnr,
            uttaksListe = request.uttaksListe.map { uttakDto ->
                Uttak(
                    ytelseType = uttakDto.ytelseType,
                    fraOgMedDato = uttakDto.fraOgMedDato,
                    uttaksgrad = uttakDto.uttaksgrad
                )
            },
            fremtidigInntektsListe = request.fremtidigInntektListe.map { fremtidigInntektDto ->
                FremtidigInntekt(
                    fraOgMedDato = fremtidigInntektDto.fraOgMedDato,
                    arligInntekt = fremtidigInntektDto.aarligInntekt
                )
            },
            arIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )

    fun mapToResponse(response: KLPSimulerTjenestepensjonResponse) =
        SimulertTjenestepensjon(
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingsListe.map { Utbetalingsperiode(it.fraOgMedDato, it.manedligUtbetaling, it.ytelseType) },
            aarsakIngenUtbetaling = response.arsakIngenUtbetaling
        )
}