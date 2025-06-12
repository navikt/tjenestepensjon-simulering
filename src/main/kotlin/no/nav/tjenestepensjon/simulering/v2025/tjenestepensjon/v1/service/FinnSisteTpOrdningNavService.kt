package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningMedDato
import org.springframework.stereotype.Service

@Service
class FinnSisteTpOrdningNavService : FinnSisteTpOrdningService {
    override fun finnSisteOrdningKandidater(tpOrdninger: List<TpOrdningMedDato>): List<String> {
        return tpOrdninger
            .sortedWith(
                compareByDescending<TpOrdningMedDato> { it.datoSistOpptjening == null }
                    .thenByDescending { it.datoSistOpptjening }
            )
            .map { it.tpNr }
    }

}