package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.pen.FremtidigInntekt
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AFPOffentligLivsvarigSimuleringService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SammenlignAFPService(private val afp: AFPOffentligLivsvarigSimuleringService) {
    private val log = KotlinLogging.logger {}
    fun sammenlignOgLoggAfp(request: SimulerTjenestepensjonRequestDto, utbetalingsperiode: List<Utbetalingsperiode>) {
        val afpLokal = afp.simuler(
            SimulerAFPOffentligLivsvarigRequest(
                fnr = request.pid,
                fom = request.uttaksdato,
                fodselsdato = request.foedselsdato,
                fremtidigeInntekter = listOf(
                    opprettNaaverendeInntektFoerUttak(request),
                    FremtidigInntekt(
                        0,
                        request.uttaksdato
                    )
                ),
            )
        )
        val afpFraTpOrdning = utbetalingsperiode.filter { it.ytelseType == "OAFP" }
        log.info { "AFP fra Tp ordning: $afpFraTpOrdning \n AFP fra lokal $afpLokal" }
    }

    private fun opprettNaaverendeInntektFoerUttak(request: SimulerTjenestepensjonRequestDto) = FremtidigInntekt(
        request.sisteInntekt,
        fjorAarSomManglerOpptjeningIPopp()
    )

    private fun fjorAarSomManglerOpptjeningIPopp(): LocalDate = LocalDate.now().minusYears(1).withDayOfYear(1)
}
