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

    const val PROVIDER_FULLT_NAVN = "Statens pensjonskasse"

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto): SPKSimulerTjenestepensjonRequest {
        return request.fremtidigeInntekter
            ?.let { mapToRequestV2(request) }
            ?: mapToRequestV1(request)
    }

    private fun mapToRequestV1(request: SimulerTjenestepensjonRequestDto) = SPKSimulerTjenestepensjonRequest(
        personId = request.pid,
        uttaksListe = opprettUttaksliste(request),
        fremtidigInntektListe = listOf(
            opprettNaaverendeInntektFoerUttak(request),
            FremtidigInntekt(
                fraOgMedDato = request.uttaksdato,
                aarligInntekt = 0
            )
        ),
        aarIUtlandetEtter16 = request.aarIUtlandetEtter16,
        epsPensjon = request.epsPensjon,
        eps2G = request.eps2G,
    )

    private fun mapToRequestV2(request: SimulerTjenestepensjonRequestDto): SPKSimulerTjenestepensjonRequest {
        val fremtidigeInntekter: MutableList<FremtidigInntekt> = mutableListOf(opprettNaaverendeInntektFoerUttak(request))
        fremtidigeInntekter.addAll(request.fremtidigeInntekter?.map {
            FremtidigInntekt(
                fraOgMedDato = it.fraOgMed,
                aarligInntekt = it.aarligInntekt
            )
        } ?: emptyList())
        return SPKSimulerTjenestepensjonRequest(
            personId = request.pid,
            uttaksListe = opprettUttaksliste(request),
            fremtidigInntektListe = fremtidigeInntekter,
            aarIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )
    }

    private fun opprettNaaverendeInntektFoerUttak(request: SimulerTjenestepensjonRequestDto) = FremtidigInntekt(
        fraOgMedDato = fjorAarSomManglerOpptjeningIPopp(),
        aarligInntekt = request.sisteInntekt
    )

    private fun fjorAarSomManglerOpptjeningIPopp(): LocalDate = LocalDate.now().minusYears(1).withDayOfYear(1)

    fun mapToResponse(response: SPKSimulerTjenestepensjonResponse, dto: SPKSimulerTjenestepensjonRequest? = null): SimulertTjenestepensjon {
        log.info { "Mapping response from SPK $response" }
        return SimulertTjenestepensjon(
            tpLeverandoer = PROVIDER_FULLT_NAVN,
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingListe.flatMap { periode ->
                val fraOgMed = periode.fraOgMedDato
                periode.delytelseListe.map { Utbetalingsperiode(fraOgMed, it.maanedligBelop, it.ytelseType) }
            },
            aarsakIngenUtbetaling = response.aarsakIngenUtbetaling.map { it.statusBeskrivelse + ": " + it.ytelseType },
            betingetTjenestepensjonErInkludert = response.utbetalingListe.flatMap { it.delytelseListe }.any { it.ytelseType == "BTP" },
            erSisteOrdning = response.aarsakIngenUtbetaling.none { it.statusKode == "IKKE_SISTE_ORDNING" },
            serviceData = listOf("Request: " + dto?.toString(), "Response: $response")
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