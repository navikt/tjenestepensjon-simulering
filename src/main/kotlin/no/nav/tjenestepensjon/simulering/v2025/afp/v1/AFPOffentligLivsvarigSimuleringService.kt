package no.nav.tjenestepensjon.simulering.v2025.afp.v1

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.AfpBeregningsgrunnlag
import no.nav.tjenestepensjon.simulering.model.domain.PensjonsbeholdningMedDelingstallAlder
import no.nav.tjenestepensjon.simulering.model.domain.pen.AfpOffentligLivsvarigYtelseMedDelingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.AlderForDelingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.InntektPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.service.AFPBeholdningClient
import no.nav.tjenestepensjon.simulering.service.ReglerClient
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AlderForDelingstallBeregner.bestemAldreForDelingstall
import no.nav.tjenestepensjon.simulering.v2025.afp.v1.OffentligAFPYtelseBeregner.beregnAfpOffentligLivsvarigYtelser
import org.springframework.stereotype.Service

@Service
class AFPOffentligLivsvarigSimuleringService(val afpBeholdningClient: AFPBeholdningClient, val reglerClient: ReglerClient) {
    private val log = KotlinLogging.logger {}
    fun simuler(request: SimulerAFPOffentligLivsvarigRequest): List<AfpOffentligLivsvarigYtelseMedDelingstall> {
        val aldreForDelingstall: List<AlderForDelingstall> = bestemAldreForDelingstall(request.fodselsdato, request.fom)

        val requestToAFPBeholdninger = SimulerAFPBeholdningGrunnlagRequest(request.fnr, request.fom, request.fremtidigeInntekter.map { InntektPeriode(it.fom, it.belop) })

        val beholdningerMedAldreForDelingstall: List<PensjonsbeholdningMedDelingstallAlder> = afpBeholdningClient.simulerAFPBeholdningGrunnlag(requestToAFPBeholdninger)
            .map { periode -> PensjonsbeholdningMedDelingstallAlder(periode.pensjonsBeholdning, aldreForDelingstall.first { it.datoVedAlder.year == periode.fom.year }) }

        val delingstallListe = reglerClient.hentDelingstall(request.fodselsdato.year, beholdningerMedAldreForDelingstall.map { it.alderForDelingstall.alder })

        val beregningsgrunnlag = beholdningerMedAldreForDelingstall
            .map {
                AfpBeregningsgrunnlag(
                    it.pensjonsbeholdning,
                    it.alderForDelingstall,
                    delingstallListe.first { dt -> dt.alder == it.alderForDelingstall.alder }.delingstall
                )
            }
        log.info { "Request for beregning av AFP: $request" }
        log.info { "Beregningsgrunnlag for AFP: $beregningsgrunnlag" }

        return beregnAfpOffentligLivsvarigYtelser(beregningsgrunnlag)
    }
}
