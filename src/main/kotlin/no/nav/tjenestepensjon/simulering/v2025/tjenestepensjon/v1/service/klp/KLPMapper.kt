package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Ordning
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.SimulertTjenestepensjon
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.LoggableSimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.dto.*
import java.time.LocalDate

object KLPMapper {

    private val log = KotlinLogging.logger {}
    const val PROVIDER_FULLT_NAVN = "Kommunal Landspensjonskasse"
    const val ANNEN_TP_ORDNING_BURDE_SIMULERE = "IKKE_SISTE_ORDNING"

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto): KLPSimulerTjenestepensjonRequest {
        val fremtidigeInntekter: MutableList<FremtidigInntekt> = mutableListOf(opprettNaaverendeInntektFoerUttak(request))
        fremtidigeInntekter.addAll(request.fremtidigeInntekter?.map {
            FremtidigInntekt(
                fraOgMedDato = it.fraOgMed,
                arligInntekt = it.aarligInntekt
            )
        } ?: emptyList())
        return KLPSimulerTjenestepensjonRequest(
            personId = request.pid,
            uttaksListe = listOf(
                Uttak(
                    ytelseType = KLPYtelse.ALLE.name,
                    fraOgMedDato = request.uttaksdato,
                    uttaksgrad = 100
                )
            ),
            fremtidigInntektsListe = fremtidigeInntekter,
            arIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )
    }

    private fun opprettNaaverendeInntektFoerUttak(request: SimulerTjenestepensjonRequestDto) = FremtidigInntekt(
        fraOgMedDato = fjorAarSomManglerOpptjeningIPopp(),
        arligInntekt = request.sisteInntekt
    )

    private fun fjorAarSomManglerOpptjeningIPopp(): LocalDate = LocalDate.now().minusYears(1).withDayOfYear(1)

    fun mapToLoggableRequestDto(dto: SimulerTjenestepensjonRequestDto) =
        LoggableSimulerTjenestepensjonRequestDto(
            uttaksdato = dto.uttaksdato,
            sisteInntekt = dto.sisteInntekt,
            aarIUtlandetEtter16 = dto.aarIUtlandetEtter16,
            brukerBaOmAfp = dto.brukerBaOmAfp,
            epsPensjon = dto.epsPensjon,
            eps2G = dto.eps2G,
            fremtidigeInntekter = dto.fremtidigeInntekter
        )

    fun mapToResponse(response: KLPSimulerTjenestepensjonResponse, dto: LoggableSimulerTjenestepensjonRequestDto? = null): SimulertTjenestepensjon {
        log.info { "Mapping response from KLP $response" }
        return SimulertTjenestepensjon(
            tpLeverandoer = PROVIDER_FULLT_NAVN,
            ordningsListe = response.inkludertOrdningListe.map { Ordning(it.tpnr) },
            utbetalingsperioder = response.utbetalingsListe.map { Utbetalingsperiode(it.fraOgMedDato, it.manedligUtbetaling, it.ytelseType) },
            aarsakIngenUtbetaling = response.arsakIngenUtbetaling.map { it.statusBeskrivelse + ": " + it.ytelseType },
            betingetTjenestepensjonErInkludert = response.utbetalingsListe.any { it.ytelseType == KLPYtelse.BTP.name },
            erSisteOrdning = response.arsakIngenUtbetaling.none { it.statusKode == ANNEN_TP_ORDNING_BURDE_SIMULERE }
        ).apply {
            serviceData = listOf("Request: ${dto?.toString()}", "Response: $response")
        }
    }


}