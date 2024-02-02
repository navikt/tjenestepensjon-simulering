package no.nav.tjenestepensjon.simulering.v3.afp

import no.nav.tjenestepensjon.simulering.model.domain.pen.AFPOffentligLivsvarigYtelse
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.AFPGrunnlagBeholdningPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.InntektPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAFPBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.service.AFPBeholdningClient
import no.nav.tjenestepensjon.simulering.service.PenClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AFPOffentligLivsvarigSimuleringService(val afpBeholdningClient: AFPBeholdningClient, val penClient: PenClient) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val hoyesteAlderForDelingstall = Alder(70, 0)

    fun simuler(request: SimulerAFPOffentligLivsvarigRequest): List<AFPOffentligLivsvarigYtelse> {
        val alder: Alder =
            if (request.fom.year - request.fodselsdato.year >= hoyesteAlderForDelingstall.aar) {
                hoyesteAlderForDelingstall
            } else {
                Alder(request.fom.year - request.fodselsdato.year, request.fom.monthValue)
            }
        log.info("Henter delingstall for fødselsår: ${request.fodselsdato.year} og alder $alder")
        val dt = penClient.hentDelingstall(request.fodselsdato.year, alder)

        val requestToAFPBeholdninger = SimulerAFPBeholdningGrunnlagRequest(request.fnr, request.fom, request.fremtidigeInntekter.map { InntektPeriode(it.fom.year, it.belop) })
        log.info("Henter AFP beholdninger for request: $requestToAFPBeholdninger") //TODO fjern fnr før produksjon
        val afpBeholdningsgrunnlag = afpBeholdningClient.simulerAFPBeholdningGrunnlag(requestToAFPBeholdninger)

        log.info("Beregner AFP Offentlig Livsvarig for request: $request") //TODO fjern fnr før produksjon
        val response = beregnAfpOffentligLivsvarigYtelser(dt.delingstall, afpBeholdningsgrunnlag.afpGrunnlagBeholdninger)

        log.info("Simulering av AFP Offentlig Livsvarig for request: $request ga response: $response") //TODO fjern fnr før produksjon
        return response
    }

    private fun beregnAfpOffentligLivsvarigYtelser(
        delingstall: Double,
        afpGrunnlagBeholdninger: List<AFPGrunnlagBeholdningPeriode>
    ): List<AFPOffentligLivsvarigYtelse> {
        return afpGrunnlagBeholdninger.map {
            AFPOffentligLivsvarigYtelse(it.fom.year, OffentligAFPYtelseBeregner.beregn(it.beholdning, delingstall), it.fom, it.tom)
        }
    }
}