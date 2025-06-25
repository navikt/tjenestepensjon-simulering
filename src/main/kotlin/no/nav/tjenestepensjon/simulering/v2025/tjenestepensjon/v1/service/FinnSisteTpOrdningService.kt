package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningMedDato

interface FinnSisteTpOrdningService {

    fun finnSisteOrdningKandidater(tpOrdninger: List<TpOrdningMedDato>): List<String>

}