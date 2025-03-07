package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.pen.FremtidigInntekt
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AFPOffentligLivsvarigSimuleringService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKMapper
import org.springframework.stereotype.Service

@Service
class SammenlignAFPService(private val afp: AFPOffentligLivsvarigSimuleringService) {
    private val log = KotlinLogging.logger {}
    fun sammenlignOgLoggAfp(request: SimulerTjenestepensjonRequestDto, utbetalingsperiode: List<Utbetalingsperiode>) {
        val fremtidigInntekt = SPKMapper.mapToRequest(request).fremtidigInntektListe

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

}
