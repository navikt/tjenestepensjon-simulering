package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent

interface StillingsprosentService {
    fun getStillingsprosentListe(fnr: String, tpOrdning: TPOrdningIdDto): List<Stillingsprosent>
}