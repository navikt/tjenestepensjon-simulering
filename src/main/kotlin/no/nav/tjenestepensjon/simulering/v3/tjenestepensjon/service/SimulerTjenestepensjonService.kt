package no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.exceptions.LeveradoerNotFoundException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.service.TpClient
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.domain.SimulerTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.v3.tjenestepensjon.domain.SisteOrdning
import org.springframework.stereotype.Service

@Service
class SimulerTjenestepensjonService(
    private val sisteOrdningService: SisteOrdningService
) {
    fun simuler(request: SimulerTjenestepensjonRequest) {

        // På kort sikt, gjenbruk logikk fra eksisterende kode
        // 1. Finn alle tjenestepensjonsforhold for bruker
        // 2. Finn opptjenings-url for alle tjenestepensjonsforholdene til bruker
        // 3. Kall opptjenings-API for alle  tjenestepensjonsforholdene til bruker
        // 4. Bruk opptjening fra alle TP-leverandører i logikk for å finne siste ordning
        // 5. Kall simulerings-tjeneste på TP-leverandør som representere siste ordning
        val sisteOrdning = sisteOrdningService.finnSisteOrdning(request.fnr)
        // TODO: Gjør dette mer smooth? Feks factory pattern e.l.
        if (sisteOrdning.tpLeverandorNavn == "KLP") {
            // map fra domene til klp request og kall simulering
        } else if (sisteOrdning.tpLeverandorNavn == "SPK") {
            // map fra domene til spk request og kall simulering
        }



        // På lengre sikt (når det foreligger en "FinnSisteOrdning"-tjeneste fra SPK)
        // 1. Finn siste ordning vha kall til SPK-API
        // 2. Finn leverandørnavn som representerer siste ordning fra TP
        // 3. Slå opp i konfig for å finne simulerings-URL for TP-leverandør som representerer siste ordning
        // 4. Kall simulerings-tjeneste på TP-leverandør som representere siste ordning

        return
    }



}