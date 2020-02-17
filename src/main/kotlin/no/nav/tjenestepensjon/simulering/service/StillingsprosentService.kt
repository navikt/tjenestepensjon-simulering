package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.TPOrdningTpLeverandorMap

interface StillingsprosentService {
    fun getStillingsprosentListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): StillingsprosentResponse
    @Throws(DuplicateStillingsprosentEndDateException::class, MissingStillingsprosentException::class)
    fun getLatestFromStillingsprosent(map: TPOrdningStillingsprosentMap): TPOrdning
}