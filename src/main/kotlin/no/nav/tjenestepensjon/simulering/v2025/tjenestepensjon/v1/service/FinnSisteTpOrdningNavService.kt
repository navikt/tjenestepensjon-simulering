package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningDto
import org.springframework.stereotype.Service

@Service
class FinnSisteTpOrdningNavService : FinnSisteTpOrdningService {
    override fun finnSisteOrdningKandidater(tpOrdninger: List<TpOrdningDto>): List<String> {
        return tpOrdninger.map { it.tpNr }
    }

}