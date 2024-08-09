package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.v1.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.v1.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.TPOrdningTpLeverandorMap

interface StillingsprosentService {
    fun getStillingsprosentListe(fnr: FNR, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): StillingsprosentResponse
    @Throws(DuplicateStillingsprosentEndDateException::class, MissingStillingsprosentException::class)
    fun getLatestFromStillingsprosent(map: TPOrdningStillingsprosentMap): TPOrdningIdDto
}