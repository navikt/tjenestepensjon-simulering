package no.nav.tjenestepensjon.simulering.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v3.afp.AFPOffentligLivsvarigSimuleringService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SimuleringAFPEndpoint(val afpOffentligLivsvarigSimuleringService: AFPOffentligLivsvarigSimuleringService, val tpClient: TpClient) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/simulering/afp-offentlig-livsvarig")
    fun simulerAfpOffentligLivsvarig(@RequestBody request: SimulerAFPOffentligLivsvarigRequest): SimulerAFPOffentligLivsvarigResponse {

        logger.info { "Simulerer AFP Offentlig Livsvarig for request: $request" }
        validateRequest(request)

        return tpClient.findForhold(FNR(request.fnr))
            .map { forhold ->
                tpClient.findTssId(forhold.ordning)
                    ?.let { TPOrdning(tpId = forhold.ordning, tssId = it) }
                    ?.let { tpClient.findTpLeverandorName(it) }
            }.firstOrNull()
            ?.let {
                logger.info{"Bruker er medlem i tp-ordning med leverandør $it. Beregner AFP Offentlig"}
                SimulerAFPOffentligLivsvarigResponse(request.fnr, afpOffentligLivsvarigSimuleringService.simuler(request), it)
            } ?: SimulerAFPOffentligLivsvarigResponse(request.fnr, emptyList(), null)
    }

    private fun validateRequest(request: SimulerAFPOffentligLivsvarigRequest) {
        if (request.fodselsdato.year < 1963) {
            throw IllegalArgumentException("Fødselsdato før 1963 er ikke støttet. Fikk ${request.fodselsdato.year}")
        }
        if (request.fom.year - request.fodselsdato.year < 62) {
            throw IllegalArgumentException("Fom dato er for tidlig i forhold til alder. Fikk ${request.fom.year} og fødselsår ${request.fodselsdato.year}")
        }
    }

}
