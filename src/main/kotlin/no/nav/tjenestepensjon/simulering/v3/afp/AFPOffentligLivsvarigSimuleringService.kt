package no.nav.tjenestepensjon.simulering.v3.afp

import no.nav.tjenestepensjon.simulering.model.domain.pen.*
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
        val alder: Alder = bestemAlderForDelingstall(request)
        log.info("Henter delingstall for fødselsår: ${request.fodselsdato.year} og alder $alder")
        val dt = penClient.hentDelingstall(request.fodselsdato.year, alder)

        val requestToAFPBeholdninger = SimulerAFPBeholdningGrunnlagRequest(request.fnr, request.fom, request.fremtidigeInntekter.map { InntektPeriode(it.fom, it.belop) })
        log.info("Henter AFP beholdninger for request: $requestToAFPBeholdninger") //TODO fjern fnr før produksjon
        val afpBeholdningsgrunnlag = afpBeholdningClient.simulerAFPBeholdningGrunnlag(requestToAFPBeholdninger)

        log.info("Beregner AFP Offentlig Livsvarig for request: $request") //TODO fjern fnr før produksjon
        val response = beregnAfpOffentligLivsvarigYtelser(dt.delingstall, afpBeholdningsgrunnlag.pensjonsBeholdningsPeriodeListe)

        log.info("Simulering av AFP Offentlig Livsvarig for request: $request ga response: $response") //TODO fjern fnr før produksjon
        return response
    }

    //TODO remove this method after testing
    fun simulerUtvidetTest(request: SimulerAFPOffentligLivsvarigRequest): SimulerAFPOffentligLivsvarigResponseUtvidetForTest {
        val alder: Alder = bestemAlderForDelingstall(request)
        val dt = penClient.hentDelingstall(request.fodselsdato.year, alder)
        log.info("TEST Hentet delingstall for fødselsår: ${request.fodselsdato.year} og alder $alder og delingstall: $dt")

        val requestToAFPBeholdninger = SimulerAFPBeholdningGrunnlagRequest(request.fnr, request.fom, request.fremtidigeInntekter.map { InntektPeriode(it.fom, it.belop) })
        log.info("TEST Henter AFP beholdninger for request: $requestToAFPBeholdninger") //TODO fjern fnr før produksjon
        val afpBeholdningsgrunnlag = afpBeholdningClient.simulerAFPBeholdningGrunnlag(requestToAFPBeholdninger)

        log.info("TEST Beregner AFP Offentlig Livsvarig for request: $request") //TODO fjern fnr før produksjon
        val response = beregnAfpOffentligLivsvarigYtelserUtvidetTest(dt.delingstall, afpBeholdningsgrunnlag.pensjonsBeholdningsPeriodeListe)

        log.info("TEST Simulering av AFP Offentlig Livsvarig for request: $request ga response: $response") //TODO fjern fnr før produksjon
        return SimulerAFPOffentligLivsvarigResponseUtvidetForTest(request.fnr, response, "Leverandør", dt)
    }

    private fun beregnAfpOffentligLivsvarigYtelserUtvidetTest(
        delingstall: Double,
        afpGrunnlagBeholdninger: List<AFPGrunnlagBeholdningPeriode>
    ): List<AFPOffentligLivsvarigYtelseUtvidetTest> {
        log.info("Beregner AFP Offentlig Livsvarig for request: $afpGrunnlagBeholdninger")
        return afpGrunnlagBeholdninger.map {
            AFPOffentligLivsvarigYtelseUtvidetTest(it.fom.year, OffentligAFPYtelseBeregner.beregn(it.pensjonsBeholdning, delingstall), it.fom, it.pensjonsBeholdning)
        }
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
                Alder(request.fom.year - request.fodselsdato.year, request.fom.monthValue)
            }
    }
}