package no.nav.tjenestepensjon.simulering.rest

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigRequest
import no.nav.tjenestepensjon.simulering.model.domain.pen.SimulerAFPOffentligLivsvarigResponse
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v3.afp.AfpOffentligLivsvarigSimuleringService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SimuleringAfpEndpoint(val afpOffentligLivsvarigSimuleringService: AfpOffentligLivsvarigSimuleringService, val tpClient: TpClient) {

    @PostMapping("/simulering/afpOffentligLivsvarig")
    fun simulerAfpOffentligLivsvarig(request: SimulerAFPOffentligLivsvarigRequest): SimulerAFPOffentligLivsvarigResponse {

        LOG.info("Simulerer AFP Offentlig Livsvarig for request: $request")

        return tpClient.findForhold(FNR(request.fnr))
            .map { forhold ->
                tpClient.findTssId(forhold.ordning)
                    ?.let { TPOrdning(tpId = forhold.ordning, tssId = it) }
                    ?.let { tpClient.findTpLeverandorName(it) }
            }.firstOrNull()
            ?.let {
                SimulerAFPOffentligLivsvarigResponse(request.fnr, afpOffentligLivsvarigSimuleringService.simuler(request), it)
            } ?: SimulerAFPOffentligLivsvarigResponse(request.fnr, emptyList(), null)
    }

    companion object {
        @JvmStatic
        private val LOG = LoggerFactory.getLogger(AfpOffentligLivsvarigSimuleringService::class.java)
    }
}
