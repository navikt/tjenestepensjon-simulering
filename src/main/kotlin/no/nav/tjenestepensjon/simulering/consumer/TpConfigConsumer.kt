package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

interface TpConfigConsumer {
    fun findTpLeverandor(tpOrdning: TPOrdning): String
}