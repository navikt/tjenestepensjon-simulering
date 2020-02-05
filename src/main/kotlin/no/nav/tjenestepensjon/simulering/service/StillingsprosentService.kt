package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent

interface StillingsprosentService {
    fun getStillingsprosentListe(fnr: FNR, tpOrdningAndLeverandorMap: Map<TPOrdning, TpLeverandor>): StillingsprosentResponse
    @Throws(DuplicateStillingsprosentEndDateException::class, MissingStillingsprosentException::class)
    fun getLatestFromStillingsprosent(map: Map<TPOrdning, List<Stillingsprosent>>): TPOrdning
}