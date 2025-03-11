package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.pen.FremtidigInntekt
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AFPOffentligLivsvarigSimuleringService
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.dto.request.SimulerTjenestepensjonRequestDto
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKMapper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class SammenlignAFPService(private val afp: AFPOffentligLivsvarigSimuleringService) {
    private val log = KotlinLogging.logger {}

    @Async("loggingExecutor")
    fun sammenlignOgLoggAfp(request: SimulerTjenestepensjonRequestDto, utbetalingsperiode: List<Utbetalingsperiode>) {
        val fremtidigInntekt = SPKMapper.mapToRequest(request).fremtidigInntektListe

        val simuleringRequest = SimulerAFPOffentligLivsvarigRequest(
            fnr = request.pid,
            fom = request.uttaksdato,
            fodselsdato = request.foedselsdato,
            fremtidigeInntekter = fremtidigInntekt.map { FremtidigInntekt(it.aarligInntekt, it.fraOgMedDato ) }
        )

        val afpLokal = afp.simuler(simuleringRequest)

        val afpFraTpOrdning = utbetalingsperiode.filter { it.ytelseType == "OAFP" }
        log.info { "Request til Tp ordning AFP: $request" +
                "\nRequest for Nav AFP: $simuleringRequest" +
                "\nAFP fra Tp ordning: $afpFraTpOrdning" +
                "\nAFP fra Nav $afpLokal" }
    }
}

