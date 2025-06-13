package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent

interface StillingsprosentService {
    fun getStillingsprosentListe(fnr: String, tpOrdning: TpOrdningFullDto): List<Stillingsprosent>
}