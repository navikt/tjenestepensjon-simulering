package no.nav.tjenestepensjon.simulering.v3.afp

import no.nav.tjenestepensjon.simulering.model.domain.pen.AfpOffentligLivsvarigYtelse
import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.popp.AfpGrunnlagBeholdningPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.InntektPeriode
import no.nav.tjenestepensjon.simulering.model.domain.popp.SimulerAfpBeholdningGrunnlagRequest
import no.nav.tjenestepensjon.simulering.service.AFPBeholdningClient
import no.nav.tjenestepensjon.simulering.service.PenClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AfpOffentligLivsvarigSimuleringService(val afpBeholdningClient: AFPBeholdningClient, val penClient: PenClient) {

    fun simuler(request: SimulerAFPOffentligLivsvarigRequest): List<AfpOffentligLivsvarigYtelse> {
        return request.let {
            SimulerAfpBeholdningGrunnlagRequest(it.fnr, it.fom, it.fremtidigeInntekter.map { InntektPeriode(it.fraOgMed.year, it.belop) })
        }
            .run {
                LOG.info("Henter AFP beholdninger for request: $this") //TODO fjern fnr før produksjon
                afpBeholdningClient.simulerAfpBeholdningGrunnlag(this)
            }
            ?.afpGrunnlagBeholdninger
            ?.let {
                LOG.info("Beregner AFP Offentlig Livsvarig for request: $request") //TODO fjern fnr før produksjon
                val response = beregnAfpOffentligLivsvarigYtelser(request, it)
                LOG.info("Simulering av AFP Offentlig Livsvarig for request: $request ga response: $response") //TODO fjern fnr før produksjon
                return@let response
            } ?: throw RuntimeException("Noe gikk galt ved henting av beholdninger fra POPP")
    }

    private fun beregnAfpOffentligLivsvarigYtelser(
        request: SimulerAFPOffentligLivsvarigRequest,
        afpGrunnlagBeholdninger: List<AfpGrunnlagBeholdningPeriode>
    ): List<AfpOffentligLivsvarigYtelse> {

        val alder: Alder =
            if (request.fom.year - request.fodselsdato.year >= 70) {
                Alder(70, 0)
            } else {
                Alder(request.fom.year - request.fodselsdato.year, request.fom.monthValue)
            }
        LOG.info("Henter delingstall for fødselsår: ${request.fodselsdato.year} og alder $alder")
        val dt = penClient.hentDelingstall(request.fodselsdato.year, alder)
        return afpGrunnlagBeholdninger.map {
            AfpOffentligLivsvarigYtelse(it.fraOgMedDato.year, (it.beholdning * 0.0421 * 18.1 / dt.delingstall), it.fraOgMedDato, it.tilOgMedDato)
        }
    }

    companion object {
        @JvmStatic
        private val LOG = LoggerFactory.getLogger(AfpOffentligLivsvarigSimuleringService::class.java)
    }
}