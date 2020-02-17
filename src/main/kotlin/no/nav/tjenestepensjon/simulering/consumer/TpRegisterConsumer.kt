package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR

interface TpRegisterConsumer {
    @Throws(NoTpOrdningerFoundException::class)
    fun getTpOrdningerForPerson(fnr: FNR): List<TPOrdning>
}