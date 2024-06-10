package no.nav.tjenestepensjon.simulering.v3.afp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.AfpBeregningsgrunnlag
import no.nav.tjenestepensjon.simulering.model.domain.PensjonsbeholdningMedDelingstallAlder
import no.nav.tjenestepensjon.simulering.model.domain.pen.AfpOffentligLivsvarigYtelseMedDelingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.AlderForDelingstall
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.InntektPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.service.AFPBeholdningClient
import no.nav.tjenestepensjon.simulering.service.PenClient
import org.springframework.stereotype.Service

@Service
class AFPOffentligLivsvarigSimuleringService(val afpBeholdningClient: AFPBeholdningClient, val penClient: PenClient) {
    private val log = KotlinLogging.logger {}

    fun simuler(request: SimulerAFPOffentligLivsvarigRequest): List<AfpOffentligLivsvarigYtelseMedDelingstall> {
        val aldreForDelingstall: List<AlderForDelingstall> = AlderForDelingstallBeregner.bestemAldreForDelingstall(request.fodselsdato, request.fom)
        log.info { "Alder for delingstall: $aldreForDelingstall"}

        val requestToAFPBeholdninger = SimulerAFPBeholdningGrunnlagRequest(request.fnr, request.fom, request.fremtidigeInntekter.map { InntektPeriode(it.fom, it.belop) })
        val beholdningerMedAldreForDelingstall: List<PensjonsbeholdningMedDelingstallAlder> = afpBeholdningClient.simulerAFPBeholdningGrunnlag(requestToAFPBeholdninger)
            .map { periode -> PensjonsbeholdningMedDelingstallAlder(periode.pensjonsBeholdning, aldreForDelingstall.first { it.datoVedAlder.year == periode.fom.year }) }
        log.info { "Beholdninger med alder for delingstall: $beholdningerMedAldreForDelingstall"}

        val delingstallListe = penClient.hentDelingstall(request.fodselsdato.year, beholdningerMedAldreForDelingstall.map { it.alderForDelingstall.alder }.toList())
        log.info { "Delingstall: $delingstallListe"}

        val beregningsgrunnlag = beholdningerMedAldreForDelingstall
            .map {
                AfpBeregningsgrunnlag(
                    it.pensjonsbeholdning,
                    it.alderForDelingstall,
                    delingstallListe.first { dt -> dt.alder == it.alderForDelingstall.alder }.delingstall
                )
            }
        log.info { "Beregningsgrunnlag: $beregningsgrunnlag"}

        return OffentligAFPYtelseBeregner.beregnAfpOffentligLivsvarigYtelser(beregningsgrunnlag)
    }
}