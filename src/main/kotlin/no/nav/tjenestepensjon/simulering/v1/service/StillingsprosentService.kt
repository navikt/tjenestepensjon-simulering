package no.nav.tjenestepensjon.simulering.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v1.TPOrdningStillingsprosentMap
import no.nav.tjenestepensjon.simulering.v1.TPOrdningTpLeverandorMap
import no.nav.tjenestepensjon.simulering.v1.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.v1.exceptions.MissingStillingsprosentException

interface StillingsprosentService {
    fun getStillingsprosentListe(fnr: String, tpOrdningAndLeverandorMap: TPOrdningTpLeverandorMap): StillingsprosentResponse
    @Throws(DuplicateStillingsprosentEndDateException::class, MissingStillingsprosentException::class)
    fun getLatestFromStillingsprosent(map: TPOrdningStillingsprosentMap): TPOrdningIdDto
}