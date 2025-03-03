package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemUttaksalderVedDato
import no.nav.tjenestepensjon.simulering.model.domain.pen.FremtidigInntekt
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AFPOffentligLivsvarigSimuleringService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class TpUtil(private val afp: AFPOffentligLivsvarigSimuleringService) {
    private val log = KotlinLogging.logger {}
    fun grupperMedDatoFra(utbetalingsliste: List<Utbetalingsperiode>, foedselsdato: LocalDate): List<Maanedsutbetaling> {
        return utbetalingsliste
            .groupBy { it.fom }
            .map { (datoFra, ytelser) ->
                val totalMaanedsBelop = ytelser.sumOf { it.maanedligBelop }
                Maanedsutbetaling(datoFra, bestemUttaksalderVedDato(foedselsdato, datoFra), totalMaanedsBelop)
            }
            .sortedBy { it.fraOgMedDato }
    }

    fun sammenlignOgLoggAfp(request: SimulerTjenestepensjonRequestDto, utbetalingsperiode: List<Utbetalingsperiode>) {
        val afpLokal = afp.simuler(
            SimulerAFPOffentligLivsvarigRequest(
            fnr = request.pid,
            fom = request.uttaksdato,
            fodselsdato = request.foedselsdato,
            fremtidigeInntekter = request.fremtidigeInntekter?.map { FremtidigInntekt(it.aarligInntekt,it.fraOgMed) } ?: emptyList()
            )
        )
        val afpFraTpOrdning = utbetalingsperiode.filter { it.ytelseType == "OAFP" }
        log.info { "AFP fra Tp ordning: $afpFraTpOrdning \n AFP fra lokal $afpLokal" }
    }
}