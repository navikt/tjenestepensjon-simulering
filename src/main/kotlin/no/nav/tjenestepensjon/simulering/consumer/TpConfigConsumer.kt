package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning

interface TpConfigConsumer {
    fun findTpLeverandor(tpOrdning: TPOrdning): String
}