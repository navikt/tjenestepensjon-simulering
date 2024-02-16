package no.nav.tjenestepensjon.simulering.v3.afp

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.pen.AFPOffentligLivsvarigYtelse
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.AFPGrunnlagBeholdningPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.InntektPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.service.AFPBeholdningClient
import no.nav.tjenestepensjon.simulering.service.PenClient
import org.springframework.stereotype.Service

@Service
class AFPOffentligLivsvarigSimuleringService(val afpBeholdningClient: AFPBeholdningClient, val penClient: PenClient) {
    private val log = KotlinLogging.logger {}
    private val hoyesteAlderForDelingstall = Alder(70, 0)

    fun simuler(request: SimulerAFPOffentligLivsvarigRequest): List<AFPOffentligLivsvarigYtelse> {
        val alder: Alder = bestemAlderForDelingstall(request)
        log.info { "Henter delingstall for fødselsår: ${request.fodselsdato.year} og alder $alder" }
        val dt = penClient.hentDelingstall(request.fodselsdato.year, alder)

        val requestToAFPBeholdninger = SimulerAFPBeholdningGrunnlagRequest(request.fnr, request.fom, request.fremtidigeInntekter.map { InntektPeriode(it.fom, it.belop) })
        log.info { "Henter AFP beholdninger for request: $requestToAFPBeholdninger" } //TODO fjern fnr før produksjon
        val afpBeholdningsgrunnlag = afpBeholdningClient.simulerAFPBeholdningGrunnlag(requestToAFPBeholdninger)

        log.info { "Beregner AFP Offentlig Livsvarig for request: $request" } //TODO fjern fnr før produksjon
        val response = beregnAfpOffentligLivsvarigYtelser(dt.delingstall, afpBeholdningsgrunnlag.pensjonsBeholdningsPeriodeListe)

        log.info { "Simulering av AFP Offentlig Livsvarig for request: $request ga response: $response" } //TODO fjern fnr før produksjon
        return response
    }

    private fun beregnAfpOffentligLivsvarigYtelser(
        delingstall: Double,
        afpGrunnlagBeholdninger: List<AFPGrunnlagBeholdningPeriode>
    ): List<AFPOffentligLivsvarigYtelse> {
        return afpGrunnlagBeholdninger.map {
            AFPOffentligLivsvarigYtelse(it.fom.year, OffentligAFPYtelseBeregner.beregn(it.pensjonsBeholdning, delingstall), it.fom)
        }
    }

    private fun bestemAlderForDelingstall(request: SimulerAFPOffentligLivsvarigRequest): Alder {
        return if (request.fom.year - request.fodselsdato.year >= hoyesteAlderForDelingstall.aar) {
            hoyesteAlderForDelingstall
        } else {
            Alder(request.fom.year - request.fodselsdato.year, request.fom.monthValue - 1) //x år og 0..11 måneder gammel
        }
    }
}