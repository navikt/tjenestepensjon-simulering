package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.pen.FremtidigInntekt
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AFPOffentligLivsvarigSimuleringService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKMapper.opprettUttaksliste
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.SPKSimulerTjenestepensjonRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

@Service
class SammenlignAFPService(private val afp: AFPOffentligLivsvarigSimuleringService) {
    private val log = KotlinLogging.logger {}
    fun sammenlignOgLoggAfp(request: SimulerTjenestepensjonRequestDto, utbetalingsperiode: List<Utbetalingsperiode>) {
        val fremtidigInntekt = mapToRequest(request).fremtidigInntektListe

        val afpLokal = afp.simuler(
            SimulerAFPOffentligLivsvarigRequest(
                fnr = request.pid,
                fom = request.uttaksdato,
                fodselsdato = request.foedselsdato,
                fremtidigeInntekter = fremtidigInntekt.map { FremtidigInntekt(it.aarligInntekt, it.fraOgMedDato ) }
            )
        )
        val afpFraTpOrdning = utbetalingsperiode.filter { it.ytelseType == "OAFP" }
        log.info { "AFP fra Tp ordning: $afpFraTpOrdning \n AFP fra lokal $afpLokal" }
    }

    fun mapToRequest(request: SimulerTjenestepensjonRequestDto): SPKSimulerTjenestepensjonRequest {
        return request.fremtidigeInntekter
            ?.let { mapToRequestV2(request) }
            ?: mapToRequestV1(request)
    }

    private fun mapToRequestV1(request: SimulerTjenestepensjonRequestDto): SPKSimulerTjenestepensjonRequest {
        val fom = fjorAarSomManglerOpptjeningIPopp()
        val til = request.foedselsdato.plusYears(62)

        val fremtidigeInntekter = genererAarligInntektListe(fom, til, request.sisteInntekt) + no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.FremtidigInntekt(
            fraOgMedDato = request.uttaksdato,
            aarligInntekt = 0
        )

        return SPKSimulerTjenestepensjonRequest(
            personId = request.pid,
            uttaksListe = opprettUttaksliste(request),
            fremtidigInntektListe = fremtidigeInntekter,
            aarIUtlandetEtter16 = request.aarIUtlandetEtter16,
            epsPensjon = request.epsPensjon,
            eps2G = request.eps2G,
        )
    }

    private fun mapToRequestV2(request: SimulerTjenestepensjonRequestDto): SPKSimulerTjenestepensjonRequest {
        val fremtidigeInntekter: MutableList<no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.FremtidigInntekt> = mutableListOf(opprettNaaverendeInntektFoerUttak(request))
        fremtidigeInntekter.addAll(request.fremtidigeInntekter?.map {
            no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.FremtidigInntekt(
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

    private fun opprettNaaverendeInntektFoerUttak(request: SimulerTjenestepensjonRequestDto) =
        no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.FremtidigInntekt(
            fraOgMedDato = fjorAarSomManglerOpptjeningIPopp(),
            aarligInntekt = request.sisteInntekt
        )

    private fun fjorAarSomManglerOpptjeningIPopp(): LocalDate = LocalDate.now().minusYears(1).withDayOfYear(1)

    private fun genererAarligInntektListe(fom: LocalDate, til: LocalDate, aarligBeloep: Int): List<no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.FremtidigInntekt> =
        fom.datesUntil(til, Period.ofYears(1))
            .map {
                no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto.FremtidigInntekt(
                    fraOgMedDato = it.withDayOfYear(1),
                    aarligInntekt = aarligBeloep
                )
            }
            .toList()

}

